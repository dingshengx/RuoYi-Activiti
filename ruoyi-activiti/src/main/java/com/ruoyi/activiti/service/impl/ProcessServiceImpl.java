package com.ruoyi.activiti.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.activiti.constants.ProcessConstants;
import com.ruoyi.activiti.constants.ProcessStatus;
import com.ruoyi.activiti.domain.Workflow;
import com.ruoyi.activiti.domain.dto.ApprovalInfoDTO;
import com.ruoyi.activiti.domain.dto.StartWorkflowDTO;
import com.ruoyi.activiti.service.DynamicJumpCmd;
import com.ruoyi.activiti.service.IProcessService;
import com.ruoyi.activiti.service.IWorkflowService;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.system.service.ISysUserService;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProcessServiceImpl implements IProcessService {
    private static final Logger log = LoggerFactory.getLogger(ProcessServiceImpl.class);
    @Autowired
    private ISysDictTypeService dictTypeService;
    @Autowired
    private IWorkflowService workflowService;
    @Autowired
    private ISysUserService userService;


    @Override
    public String submit(StartWorkflowDTO startWorkflowDTO) {
        //检查是否已发起过流程
        boolean existed = workflowService.checkExistedWorkflow(startWorkflowDTO.getBizModel(),
                startWorkflowDTO.getBizId());
        Assert.isFalse(existed, "发起流程失败，该单据已存在流程，不能重复发起流程。");
        //设置申请人
        String applicant = (String) startWorkflowDTO.getVariables().get(ProcessConstants.APPLICANT_ASSIGNEE_VARIABLE);
        if (applicant == null || applicant.isEmpty()) {
            startWorkflowDTO.getVariables().put(ProcessConstants.APPLICANT_ASSIGNEE_VARIABLE,
                    SecurityUtils.getUsername());
        }
        //发起流程
        String processInstanceId = this.doSubmit(startWorkflowDTO.getProcessDefinitionKey(),
                startWorkflowDTO.getBusinessKey(),
                startWorkflowDTO.getVariables());
        //添加流程信息
        addWorkflowInfo(processInstanceId, startWorkflowDTO);
        return processInstanceId;
    }

    @Override
    public void approval(ApprovalInfoDTO approvalInfoDTO) {
        //查询流程信息
        Workflow workflow = workflowService.getOne(approvalInfoDTO.getBizModel(), approvalInfoDTO.getBizId());
        Assert.notNull(workflow, "找不到流程信息，请确认是否发起流程。");
        //构建参数
        approvalInfoDTO.setAssignee(SecurityUtils.getUsername());
        approvalInfoDTO.setProcessInstanceId(workflow.getProcInstId());
        //提交审批
        doApproval(approvalInfoDTO);
        //回写流程信息
        updateWorkflowInfo(workflow.getProcInstId());

    }


    private String doSubmit(String processDefinitionKey, String businessKey, Map<String, Object> variables) {
        //内部流程变量
        Map<String, Object> variables_ = new HashMap<>();
        // 1:得到ProcessEngine对象
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 2:获取RuntimeService服务
        RuntimeService runtimeService = engine.getRuntimeService();
        List<SysDictData> list = dictTypeService.selectDictDataByType(ProcessConstants.DICT_TYPE_WORKFLOW_ROLE);
        /*获取字典的工作流角色信息放到流程变量里*/
        list.forEach(sysDictData -> {
            variables_.put(sysDictData.getDictLabel(), sysDictData.getDictValue());
        });
        //添加方法参数variables放到到内部流程变量，原来相同key的会被覆盖
        variables_.putAll(variables);
        // 3:根据KEY启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey,
                variables_);
        log.info("启动流程 name:{},processInstanceId:{},ActivityId:{}", processInstance.getName(), processInstance.getId(),
                processInstance.getActivityId());

        /*流程提交后如果是申请人发起节点（task_applicant_node）就继续往下走*/
        Task task = this.getCurrentTaskByProcessInstanceId(processInstance.getProcessInstanceId());
        log.info("taskName:{},taskId:{},taskDefinitionKey:{}", task.getName(), task.getId(),
                task.getTaskDefinitionKey());
        if (ProcessConstants.TASK_APPLY_NODE.equals(task.getTaskDefinitionKey())) {
            engine.getTaskService().complete(task.getId());
        }
        return processInstance.getId();
    }


    public void doApproval(ApprovalInfoDTO approvalInfoDTO) {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 做任务申请 也需要通过 TaskService 来实现
        TaskService taskService = engine.getTaskService();
        // 根据当前登录用户查询出对应的待办信息
        List<Task> tasks =
                taskService.createTaskQuery().processInstanceId(approvalInfoDTO.getProcessInstanceId()).taskAssignee(approvalInfoDTO.getAssignee()).list();
        Assert.notEmpty(tasks, "您不是当前的审批人，无权限审批。");
        Task task = tasks.get(0);
        log.info("提交审批 当前任务 name:{},taskId:{}", task.getName(), task.getId());
        if (approvalInfoDTO.getAttachments() != null) {
            approvalInfoDTO.getAttachments().forEach(attachmentDTO -> {
                taskService.createAttachment(attachmentDTO.getType(), task.getId(), task.getProcessInstanceId(),
                        attachmentDTO.getFileName(),
                        attachmentDTO.getDescription(), attachmentDTO.getFileId());
            });
        }
        /*增加审批备注 !!--taskService.addComment必须放在taskService.complete()前面--!!*/
        taskService.addComment(task.getId(), task.getProcessInstanceId(), "comment",
                approvalInfoDTO.getRemark());
        /*审批意见state保存一条备注记录，用于历史审批记录使用。*/
        if (approvalInfoDTO.getState() != null) {
            taskService.addComment(task.getId(), task.getProcessInstanceId(), "state",
                    approvalInfoDTO.getState().toString());
        }
        taskService.complete(task.getId(), approvalInfoDTO.getVariables(), Boolean.TRUE);
    }

    @Override
    public void rollback(ApprovalInfoDTO approvalInfoDTO) {
        //查询流程信息
        Workflow workflow = workflowService.getOne(approvalInfoDTO.getBizModel(), approvalInfoDTO.getBizId());
        Assert.notNull(workflow, "找不到流程信息，请确认是否发起流程。");
        //构建参数
        approvalInfoDTO.setAssignee(SecurityUtils.getUsername());
        approvalInfoDTO.setProcessInstanceId(workflow.getProcInstId());
        //提交退回
        doRollback(approvalInfoDTO);
        //回写流程信息
        updateWorkflowInfo(workflow.getProcInstId());
    }

    public void doRollback(ApprovalInfoDTO approvalInfoDTO) {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 做任务申请 也需要通过 TaskService 来实现
        TaskService taskService = engine.getTaskService();
        //查询当前任务
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(approvalInfoDTO.getProcessInstanceId()).taskAssignee(approvalInfoDTO.getAssignee()).list();
        Assert.notEmpty(tasks, "您不是当前的审批人，无权限审批。");
        Task currentTask = tasks.get(0);
        Assert.notNull(currentTask.getAssignee(), "当前流程审批人为空，请跟管理员确认流程是否正确。");
        Assert.isTrue(currentTask.getAssignee().equals(approvalInfoDTO.getAssignee()), "您不是当前的审批人，无权限审批。");
        //查询上一个任务
        String preTaskDefinitionKey = findPreTask(approvalInfoDTO.getProcessInstanceId());
        Assert.notNull(preTaskDefinitionKey, "找不到上一个节点任务，请确定流程是否发起。");
        // 添加附件
        if (approvalInfoDTO.getAttachments() != null) {
            approvalInfoDTO.getAttachments()
                    .forEach(attachmentDTO -> {
                        taskService.createAttachment(attachmentDTO.getType(), currentTask.getId(),
                                currentTask.getProcessInstanceId(), attachmentDTO.getFileName(),
                                attachmentDTO.getDescription(), attachmentDTO.getFileId());
                    });
        }
        /*增加审批备注*/
        taskService.addComment(currentTask.getId(), currentTask.getProcessInstanceId(), "comment",
                approvalInfoDTO.getRemark());
        /*审批意见state保存一条备注记录，用于历史审批记录使用。*/
        taskService.addComment(currentTask.getId(), currentTask.getProcessInstanceId(), "state",
                approvalInfoDTO.getState()
                        .toString());
        log.info("流程跳转： 当前节点:{},目标节点:{}", currentTask.getTaskDefinitionKey(), preTaskDefinitionKey);
        //调用流程跳转方法
        this.jump(approvalInfoDTO.getProcessInstanceId(), currentTask.getTaskDefinitionKey(), preTaskDefinitionKey);

    }

    /**
     * 添加流程信息
     *
     * @param processInstanceId 流程实例ID
     * @param startWorkflowDTO  流程发起信息
     */
    private void addWorkflowInfo(String processInstanceId, StartWorkflowDTO startWorkflowDTO) {
        Workflow workflow = Optional.ofNullable(workflowService.getOne(startWorkflowDTO.getBizModel(), startWorkflowDTO.getBizId()))
                                    .orElse(new Workflow());
        // 查询当前代办
        List<Task> currentTasks = this.getCurrentTasksByProcessInstanceId(processInstanceId);
        // 设置待审批人相关信息
        setApproverData(currentTasks, workflow);
        // 设置业务状态
        workflow.setWorkflowStatus(this.isProcessInstanceEnded(processInstanceId) ?
                                           ProcessStatus.APPROVED.getCode() :
                                           ProcessStatus.PENDING_APPROVAL.getCode());
        // 设置其他基础信息
        workflow.setProcInstId(processInstanceId);
        workflow.setBizId(startWorkflowDTO.getBizId());
        workflow.setBizModel(startWorkflowDTO.getBizModel());
        workflow.setApplicant(SecurityUtils.getUsername());
        workflow.setApplicantId(SecurityUtils.getUserId());
        workflow.setApplicantName(SecurityUtils.getNickName());
        workflow.setApplyDept(SecurityUtils.getLoginUser().getUser().getDept().getDeptName());
        workflow.setApplyTime(LocalDateTime.now());
        workflowService.saveOrUpdate(workflow);
    }

    /**
     * 审批通过或者驳回后回写数据到公共流程表
     *
     * @param processInstanceId 流程实例
     */
    private void updateWorkflowInfo(String processInstanceId) {
        Workflow workflow = workflowService.getOne(processInstanceId);
        List<Task> currentTasks = this.getCurrentTasksByProcessInstanceId(processInstanceId);
        // 设置待审批人相关信息
        setApproverData(currentTasks, workflow);
        // 设置已审批人相关信息
        workflow.setApprovedId(SecurityUtils.getUserId());
        workflow.setApprovedBy(SecurityUtils.getUsername());
        workflow.setApprovedName(SecurityUtils.getNickName());
        workflow.setApprovedTime(LocalDateTime.now());
        // 设置状态
        workflow.setWorkflowStatus(this.isProcessInstanceEnded(processInstanceId) ?
                                           ProcessStatus.APPROVED.getCode() :
                                           getWorkflowStatus(currentTasks.get(0)
                                                                         .getTaskDefinitionKey(), workflow.getWorkflowStatus()));
        // 更新数据
        workflowService.updateById(workflow);
    }

    /**
     * 设置待审批人相关信息
     * 跟状态无关，只关心当前的代办任务
     * 更新为最新的代办任务对应的受理人信息即可
     *
     * @param currentTasks      当前任务列表
     * @param workflow          流程数据
     */
    private void setApproverData(List<Task> currentTasks, Workflow workflow) {
        if (CollectionUtil.isNotEmpty(currentTasks)) {
            String[] userNames = currentTasks.stream().map(Task::getAssignee).toArray(String[]::new);
            List<SysUser> tempUsers = userService.selectUserByUserNames(userNames);
            if(CollectionUtil.isNotEmpty(tempUsers)){
                String approverId = StrUtil.join(",", tempUsers.stream()
                                                               .map(SysUser::getUserId)
                                                               .collect(Collectors.toList()));
                String approverName = StrUtil.join(",", tempUsers.stream()
                                                                 .map(SysUser::getNickName)
                                                                 .collect(Collectors.toList()));
                String approverBy = StrUtil.join(",", tempUsers.stream()
                                                               .map(SysUser::getUserName)
                                                               .collect(Collectors.toList()));
                workflow.setApproverBy(approverBy);
                workflow.setApproverName(approverName);
                workflow.setApproverId(approverId);
            }
            workflow.setCurrentNodeKey(currentTasks.get(0).getTaskDefinitionKey());
            workflow.setCurrentNodeName(currentTasks.get(0).getName());
        } else {
            workflow.setCurrentNodeKey("");
            workflow.setCurrentNodeName("");
            workflow.setApproverName("");
            workflow.setApproverBy("");
            workflow.setApproverId("");
        }
    }

    /**
     * 根据代办节点和上一节点状态判定当前流程状态
     * 用于审批（通过或者驳回）的场景，不适用于发起
     * 如果当前节点是发起人节点，流程状态设定为待重申
     * 如果当前节点不是发起人节点，但是上一个节点状态为待重申（待重申只会有审批通过不会有驳回），流程状态设定为待审批
     * 其他的默认为审批中
     *
     * @param currentNodeKey       当前节点key
     * @param beforeWorkflowStatus 上一节点状态（修改前状态）
     */
    private static Integer getWorkflowStatus(String currentNodeKey, Integer beforeWorkflowStatus) {
        if (StrUtil.equals(currentNodeKey, ProcessConstants.TASK_APPLY_NODE)) {
            return ProcessStatus.TO_BE_REITERATED.getCode();
        } else if (beforeWorkflowStatus == ProcessStatus.TO_BE_REITERATED.getCode()) {
            return ProcessStatus.PENDING_APPROVAL.getCode();
        }
        return ProcessStatus.IN_APPROVAL.getCode();
    }

    /**
     * 获取当前任务
     *
     * @param processInstanceId 流程实例ID
     * @return 任务
     */
    @Override
    public Task getCurrentTaskByProcessInstanceId(String processInstanceId) {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        //返回当前任务
        List<Task> tasks =
                engine.getTaskService().createTaskQuery().processInstanceId(processInstanceId).active().list();
        if (!tasks.isEmpty()) {
            return tasks.get(0);
        }
        return null;
    }

    @Override
    public List<Task> getCurrentTasksByProcessInstanceId(String processInstanceId) {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        //返回当前任务
        return engine.getTaskService().createTaskQuery().processInstanceId(processInstanceId).active().list();
    }

    @Override
    public boolean isProcessInstanceEnded(String processInstanceId) {
        // 创建 ProcessEngine 对象
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 根据流程实例 ID 查询流程实例
        ProcessInstance processInstance =
                engine.getRuntimeService().createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        // 判断流程实例是否结束
        return processInstance == null || processInstance.isEnded();
    }


    @Override
    public List<UserTask> getUserTasksByProcessInstanceId(String processInstanceId) {
        // 获取流程引擎
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 获取RepositoryService
        RepositoryService repositoryService = engine.getRepositoryService();

        HistoricProcessInstance processInstance =
                engine.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        // 获取BpmnModel
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        // 获取流程
        Process process = bpmnModel.getProcesses().get(0);
        List<UserTask> userTasks = new ArrayList<UserTask>();
        // 遍历流程中的节点
        for (FlowElement flowElement : process.getFlowElements()) {
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                userTasks.add(userTask);
            }
        }
        return userTasks;
    }

    /**
     * 获取流程变量（排除掉用户任务的变量）
     *
     * @param processInstanceId 流程实例ID
     * @return
     */
    @Override
    public Map<String, HistoricVariableInstance> getProcessVariables(String processInstanceId) {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        HistoricProcessInstance historicProcessInstance =
                engine.getHistoryService().createHistoricProcessInstanceQuery()
                        .processInstanceId(processInstanceId)
                        .singleResult();
        Map<String, HistoricVariableInstance> variables = new HashMap<>();
        if (historicProcessInstance != null) {
            List<HistoricVariableInstance> historicVariables =
                    engine.getHistoryService().createHistoricVariableInstanceQuery()
                            .processInstanceId(processInstanceId).excludeTaskVariables()
                            .list();
            for (HistoricVariableInstance variable : historicVariables) {
                variables.put(variable.getVariableName(), variable);
            }
        } else {
            log.error("No historic process instance found with id: {}", processInstanceId);
        }
        return variables;

    }

    /**
     * 查找最新历史任务
     *
     * @param processInstanceId 流程实例ID
     * @return 历史任务
     */
    private String findPreTask(String processInstanceId) {
        // 检查 processInstanceId 是否为空
        if (processInstanceId == null || processInstanceId.trim()
                .isEmpty()) {
            return null;
        }

        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = engine.getHistoryService();
        TaskService taskService = engine.getTaskService();

        // 获取当前任务，注意对null的处理
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId).list();
        Task currentTask = tasks.get(0);
        if (currentTask == null) {
            return null; // 如果没有当前任务，则认为无法找到历史任务
        }

        // 直接在查询时过滤掉 deleteReason 不为空的(不为空即为非驳回) 并且创建时间早于当前task
        List<HistoricTaskInstance> historyTaskList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .taskDeleteReason(null)// 过滤掉 deleteReason 不为空的记录
                .taskCreatedBefore(currentTask.getCreateTime())
                .finished()
                .list();

        // 如果历史任务列表为空，直接返回null
        if (historyTaskList.isEmpty()) {
            return null;
        }

        // 对历史任务按结束时间进行倒序排序
        historyTaskList.sort(Comparator.comparing(HistoricTaskInstance::getEndTime)
                .reversed());
        int currentSn = getTaskSn(currentTask.getProcessDefinitionId(), currentTask.getTaskDefinitionKey(),
                currentTask.getFormKey());
        // 使用Stream API 进行过滤和选择，找到第一个符合条件的历史任务
        HistoricTaskInstance previousTask = historyTaskList.stream()
                //formKey小于当前节点。assignee不能为空，空的是跳过节点。
                .filter(item -> {
                    int sn = getTaskSn(item.getProcessDefinitionId(), item.getTaskDefinitionKey(), item.getFormKey());
                    return sn < currentSn && item.getAssignee() != null;
                })
                .findFirst()
                .orElse(null);

        // 如果找到符合条件的历史任务，返回其任务定义键
        if (previousTask != null) {
            return previousTask.getTaskDefinitionKey();
        }
        return null;
    }

    /**
     * 流程跳转
     *
     * @param processInstanceId 流程实例ID
     * @param fromActivityId    跳转起始节点
     * @param toActivityId      跳转目的节点
     */
    private void jump(String processInstanceId, String fromActivityId, String toActivityId) {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        ManagementService managementService = engine.getManagementService();
        //判断是否同一个节点
        Assert.notEquals(fromActivityId, toActivityId, "不能跳到相同节点");
        //实例化自定义跳转Command类
        DynamicJumpCmd dynamicJumpCmd = new DynamicJumpCmd(processInstanceId, fromActivityId, toActivityId);
        //通过ManagementService管理服务执行自定义跳转Command类
        managementService.executeCommand(dynamicJumpCmd);
    }

    @Override
    public String getExtensionElementsProperty(String processDefinitionId, String taskDefinitionKey,
                                               String propertyName) {
        // 获取流程引擎
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 获取RepositoryService
        RepositoryService repositoryService = engine.getRepositoryService();
        // 获取BpmnModel
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        // 获取流程
        Process process = bpmnModel.getProcesses().get(0);
        List<UserTask> userTasks = new ArrayList<UserTask>();
        // 遍历流程中的节点
        for (FlowElement flowElement : process.getFlowElements()) {
            if (flowElement instanceof UserTask && taskDefinitionKey.equals(flowElement.getId())) {
                Map<String, List<ExtensionElement>> map = flowElement.getExtensionElements();
                for (String k : map.keySet()) {
                    return getExtensionElementsProperty(map.get(k), propertyName);
                }
            }
        }
        return "";
    }

    @Override
    public String getExtensionElementsProperty(Map<String, List<ExtensionElement>> taskElements, String propertyName) {
        for (String k : taskElements.keySet()) {
            return getExtensionElementsProperty(taskElements.get(k), propertyName);
        }
        return "";
    }


    private String getExtensionElementsProperty(List<ExtensionElement> elements, String propertyName) {
        ExtensionElement element = elements.get(0);
        Map<String, List<ExtensionElement>> childElements = element.getChildElements();
        for (String key : childElements.keySet()) {
            List<ExtensionElement> f1ElementList = childElements.get(key);
            ExtensionElement f1element = f1ElementList.get(0);
            if (f1element.getAttributes().isEmpty()) {
                getExtensionElementsProperty(elements, propertyName);
            } else {
                Map<String, List<ExtensionAttribute>> map = f1element.getAttributes();
                /*这数据结构比较怪异：<activiti:property name="sn" value="1" />
                name,value存在map({"name",{name:"name",value:"sn}},{"value",{name:"value",value:"3"}})里*/
                for (String k : map.keySet()) {
                    ExtensionAttribute attributeName = map.get(k).get(0);
                    if (propertyName.equals(attributeName.getValue())) {
                        ExtensionAttribute attributeValue = map.get("value").get(0);
                        return attributeValue.getValue();
                    }
                }
            }
        }
        return "";
    }

    @Override
    public int getTaskSn(Map<String, List<ExtensionElement>> taskElements, String formKey) {
        String sn = formKey;
        if (formKey == null || formKey.isEmpty()) {
            sn = this.getExtensionElementsProperty(taskElements, ProcessConstants.TASK_SN_PROPERTY_NAME);
        }
        try {
            return Integer.parseInt(sn);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return 0;

    }

    @Override
    public int getTaskSn(String processDefinitionId, String taskDefinitionKey, String formKey) {
        String sn = formKey;
        if (formKey == null || formKey.isEmpty()) {
            sn = this.getExtensionElementsProperty(processDefinitionId, taskDefinitionKey,
                    ProcessConstants.TASK_SN_PROPERTY_NAME);
        }
        try {
            return Integer.parseInt(sn);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return 0;
    }
}
