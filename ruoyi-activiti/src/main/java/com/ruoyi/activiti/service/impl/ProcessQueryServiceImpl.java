package com.ruoyi.activiti.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.activiti.constants.ProcessConstants;
import com.ruoyi.activiti.domain.vo.ApprovalProgressVO;
import com.ruoyi.activiti.domain.vo.ApprovalRecordGroupVO;
import com.ruoyi.activiti.domain.vo.ApprovalRecordVO;
import com.ruoyi.activiti.domain.vo.AttachmentVO;
import com.ruoyi.activiti.mapper.ActRuTaskMapper;
import com.ruoyi.activiti.service.IProcessQueryService;
import com.ruoyi.activiti.service.IProcessService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysUserService;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.compress.utils.Lists;
import org.apache.xmlbeans.impl.xb.xsdschema.Wildcard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 工作流任务统计、任务查询
 */
@Service
public class ProcessQueryServiceImpl implements IProcessQueryService {
    private static final Logger log = LoggerFactory.getLogger(ProcessQueryServiceImpl.class);

    @Autowired
    ActRuTaskMapper taskMapper;

    @Autowired
    private IProcessService processService;

    @Autowired
    private ISysUserService sysUserService;
    private String assignee;

    /**
     * skipExpression正则表达式
     */
    private static final String SKIP_EXPRESSION_REGEX = "^\\$\\{([a-zA-Z_][a-zA-Z0-9_]*)\\s*==\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\}$";

    /**
     * 查询已审批的任务
     *
     * @param processInstanceId 流程实例ID
     * @return
     */
    @Override
    public List<ApprovalRecordVO> selectApprovedTaskList(String processInstanceId) {
        return taskMapper.selectApprovedTaskList(processInstanceId);
    }

    /**
     * 查询已审批的任务，带附件
     *
     * @param processInstanceId 流程实例ID
     * @return
     */
    @Override
    public List<ApprovalRecordVO> selectApprovedTaskListWithAttachment(String processInstanceId) {
        List<AttachmentVO> attachments = taskMapper.selectAttachmentList(processInstanceId);
        List<ApprovalRecordVO> records = taskMapper.selectApprovedTaskList(processInstanceId);
        Map<String, List<AttachmentVO>> attachmentMap = attachments.stream()
                .collect(Collectors.groupingBy(AttachmentVO::getTaskId));
        records.forEach(record -> {
            List<AttachmentVO> tempAttachments = attachmentMap.get(record.getTaskId());
            if (tempAttachments != null && !tempAttachments.isEmpty()) {
                record.setAttachments(tempAttachments);
            }
        });
        return records;
    }

    @Override
    public List<ApprovalProgressVO> selectApprovalProgressList(String processInstanceId) {
        List<ApprovalProgressVO> progressList = new ArrayList<>();
        /*获取任务节点信息*/
        List<UserTask> userTasks = processService.getUserTasksByProcessInstanceId(processInstanceId);
        /*查找流程审批信息*/
        List<ApprovalRecordVO> records = this.selectApprovedTaskList(processInstanceId);
        Map<String, List<ApprovalRecordVO>> recordMap =
                records.stream().collect(Collectors.groupingBy(ApprovalRecordVO::getTaskDefKey,
                        Collectors.toList()));
        Task currentTask = processService.getCurrentTaskByProcessInstanceId(processInstanceId);
        Map<String, List<SysUser>> assigneeUsers = this.selectAssigneeUsersFromProcessVariables(processInstanceId);
        /*当前任务节点的序号，默认99,*/
        int currentSn = 99;
        if (currentTask != null) {
            currentSn = processService.getTaskSn(currentTask.getProcessDefinitionId(),
                    currentTask.getTaskDefinitionKey(),
                    currentTask.getFormKey());
        }
        String approveName;
        for (UserTask userTask : userTasks) {
            //执行表达式判断是否跳过
            if (executeSkipExpression(userTask, assigneeUsers)) {
                continue;
            }
            List<ApprovalRecordVO> tempRecords = recordMap.get(userTask.getId());
            ApprovalProgressVO approvalProgressVO = new ApprovalProgressVO();
            int tempSn = processService.getTaskSn(userTask.getExtensionElements(), userTask.getFormKey());
            approvalProgressVO.setTaskDefKey(userTask.getId());
            approvalProgressVO.setTaskDefName(userTask.getName());
            approvalProgressVO.setSn(String.valueOf(tempSn));
            /*审批记录小于当前节点*/
            if (tempSn < currentSn) {
                approveName = tempRecords.stream()
                        .map(ApprovalRecordVO::getApprovedName).distinct()
                        .collect(Collectors.joining(", "));
                approvalProgressVO.setFlowStatus("1");//已审批
                approvalProgressVO.setApproveName(approveName);
                approvalProgressVO.setApprovedTime(tempRecords.get(tempRecords.size() - 1).getApprovedTime());
                approvalProgressVO.setRemark(tempRecords.get(tempRecords.size() - 1).getRemark());
                progressList.add(approvalProgressVO);
            } else {
                approvalProgressVO.setFlowStatus("0");//未审批
                String assignee;
                if (userTask.hasMultiInstanceLoopCharacteristics()) {
                    assignee =
                            userTask.getLoopCharacteristics().getInputDataItem().replace("$", "").replace("{", "").replace("}",
                                    "");
                } else {
                    assignee = userTask.getAssignee().replace("$", "").replace("{", "").replace("}",
                            "");
                }
                List<SysUser> tempUsers = assigneeUsers.get(assignee);
                if (CollectionUtil.isNotEmpty(tempUsers)) {
                    String nickNames = tempUsers.stream()
                                                .map(SysUser::getNickName)
                                                .distinct()
                                                .collect(Collectors.joining(","));
                    approvalProgressVO.setApproveName(nickNames);
                } else {
                    approvalProgressVO.setApproveName("待分配");
                }
                progressList.add(approvalProgressVO);
            }
        }
        progressList.sort(Comparator.comparing(
                ApprovalProgressVO::getSn,
                Comparator.nullsFirst(Comparator.naturalOrder())));
        return progressList;
    }

    /**
     * 执行跳过表达式
     * 用于判断是否跳过当前节点
     *
     * @param userTask      用户任务
     * @param assigneeUsers 当前流程的用户变量信息
     * @return 结果
     */
    private static boolean executeSkipExpression(UserTask userTask, Map<String, List<SysUser>> assigneeUsers) {
        String skipExpression = userTask.getSkipExpression();
        // 如果不包含跳过表达式，则直接返回false
        if (StrUtil.isBlank(skipExpression)) {
            return false;
        }
        skipExpression = StrUtil.removeAll(skipExpression, StrUtil.SPACE);
        // 验证跳过表达式格式，不符合指定格式则跳过
        if (!StringUtils.validateFormat(skipExpression, SKIP_EXPRESSION_REGEX)) {
            return false;
        }

        // 提取变量名
        String variable1 = skipExpression.substring(2, skipExpression.indexOf("=="));
        String variable2 = skipExpression.substring(skipExpression.indexOf("==") + 2, skipExpression.length() - 1);

        // 根据变量名获取SysUser对象（这里要求skipExpression的两个变量名要通过待审批人的变量名去控制）
        SysUser sysUser1 = Optional.ofNullable(assigneeUsers.get(variable1))
                                   .flatMap(users -> users.isEmpty() ?
                                           Optional.empty() :
                                           Optional.of(users.get(0)))
                                   .orElse(null);
        SysUser sysUser2 = Optional.ofNullable(assigneeUsers.get(variable2))
                                   .flatMap(users -> users.isEmpty() ?
                                           Optional.empty() :
                                           Optional.of(users.get(0)))
                                   .orElse(null);

        // 比较 SysUser 的 userId 是否相等
        return ObjectUtil.isNotNull(sysUser1) && ObjectUtil.isNotNull(sysUser2) && sysUser1.getUserId()
                                                                                           .equals(sysUser2.getUserId());
    }

    /**
     * 查找已审批记录
     *
     * @param processInstanceId 流程实例ID
     * @return
     */
    @Override
    public List<ApprovalRecordGroupVO> selectApprovedRecords(String processInstanceId) {
        List<ApprovalRecordGroupVO> list = new ArrayList<>();
        /*获取任务节点信息*/
        List<UserTask> userTasks = processService.getUserTasksByProcessInstanceId(processInstanceId);
        /*查找流程审批信息*/
        List<ApprovalRecordVO> records = this.selectApprovedTaskListWithAttachment(processInstanceId);
        Map<String, List<ApprovalRecordVO>> recordMap =
                records.stream().collect(Collectors.groupingBy(ApprovalRecordVO::getTaskDefKey,
                        Collectors.toList()));

        userTasks.forEach(userTask -> {
            List<ApprovalRecordVO> tempRecords = recordMap.get(userTask.getId());
            /*排除掉申请人发起节点*/
            if (tempRecords != null && !tempRecords.isEmpty() && !ProcessConstants.TASK_APPLY_NODE.equals(userTask.getId())) {
                ApprovalRecordGroupVO recordGroupVO = new ApprovalRecordGroupVO();
                recordGroupVO.setTaskDefKey(userTask.getId());
                recordGroupVO.setTaskDefName(userTask.getName());
                recordGroupVO.setSn(userTask.getFormKey());
                recordGroupVO.setRecords(tempRecords);
                list.add(recordGroupVO);
            }
        });
        return list;
    }

    /**
     * 通过流程实例ID 查询流程变量对应的用户信息
     *
     * @param processInstanceId 程实例ID
     * @return
     */
    public Map<String, List<SysUser>> selectAssigneeUsersFromProcessVariables(String processInstanceId) {
        Map<String, List<SysUser>> sysUserMap = new HashMap<>();
        try {
            // 获取流程变量
            Map<String, HistoricVariableInstance> variables = processService.getProcessVariables(processInstanceId);
            if (variables == null || variables.isEmpty()) {
                return sysUserMap; // 直接返回空map
            }
            Map<String, List<String>> values = variables.entrySet()
                                                        .stream()
                                                        .filter(entry -> entry.getValue()
                                                                              .getValue() != null)
                                                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                                                            String value = entry.getValue()
                                                                                .getValue()
                                                                                .toString();
                                                            String type = entry.getValue()
                                                                               .getVariableTypeName();
                                                            if ("json".equals(type)) {
                                                                return JSON.parseArray(value, String.class);
                                                            } else {
                                                                return Collections.singletonList(value);
                                                            }
                                                        }));
            // 使用Set避免重复，提高查询效率
            // 执行批量查询
            List<SysUser> userList = sysUserService.selectUserByUserNames(values.values().stream()
                    .flatMap(List::stream).distinct().toArray(String[]::new));
            Map<String, SysUser> userMap = userList.stream()
                    .collect(Collectors.toMap(SysUser::getUserName, user -> user));
            // 使用流直接将用户信息映射到sysUserMap中，减少中间集合的创建
            values.forEach((key, value) -> {
                List<SysUser> users = Lists.newArrayList();
                for (String userName : value) {
                    SysUser user = userMap.get(userName);
                    users.add(user);
                }
                sysUserMap.put(key, users);
            });

            // 查询用户信息
        } catch (Exception e) {
            // 异常处理，可以记录日志或返回特定的错误信息
            log.error("Failed to select assignee users for process instance {}", processInstanceId, e);
            sysUserMap.clear(); // 可能需要清理或标记错误状态
        }
        return sysUserMap;
    }
}
