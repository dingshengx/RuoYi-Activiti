package com.ruoyi.activiti.controller;


import com.github.pagehelper.Page;
import com.ruoyi.activiti.constants.ProcessConstants;
import com.ruoyi.activiti.domain.Workflow;
import com.ruoyi.activiti.domain.dto.*;
import com.ruoyi.activiti.domain.vo.ApprovalProgressVO;
import com.ruoyi.activiti.service.IActTaskService;
import com.ruoyi.activiti.service.IProcessQueryService;
import com.ruoyi.activiti.service.IProcessService;
import com.ruoyi.activiti.service.IWorkflowService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.*;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Api(tags = "流程DEMO")
@RestController
@RequestMapping("/activiti/demo")
public class DemoController extends BaseController {
    @Autowired
    private IProcessService processService;
    @Autowired
    private IProcessQueryService processQueryService;
    @Autowired
    private IWorkflowService workflowService;

    @PostMapping("/{processDefinitionKey}/submit")
    public AjaxResult submit(@PathVariable("processDefinitionKey") String processDefinitionKey) {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(ProcessConstants.APPLICANT_ASSIGNEE_VARIABLE, this.getUsername());
        List<String> purchasingList = Arrays.asList("huishen1", "huishen2", "huishen3");
        variables.put("purchasingList", purchasingList);
        variables.put("operationDirector", "operationDirector");
        variables.put("leader", "operationDirector");
        variables.put("_ACTIVITI_SKIP_EXPRESSION_ENABLED", true);//开启表达式自动跳过功能
        long rangeRandomLong = ThreadLocalRandom.current().nextLong(0, 1000000L);
        StartWorkflowDTO startWorkflowDTO =
                StartWorkflowDTO.builder().processDefinitionKey(processDefinitionKey).bizModel("demo").bizId(rangeRandomLong).applicant(this.getUsername()).
                        applicantId(this.getUserId()).applicantName(this.getNickName()).applyDept(this.getDeptName()).variables(variables).build();
        return AjaxResult.success(processService.submit(startWorkflowDTO));
    }

    @PostMapping("/approval")
    public AjaxResult approval(@RequestBody ApprovalInfo2DTO approvalInfo2DTO) {
        Map<String, Object> variables = new HashMap<String, Object>();
        approvalInfo2DTO.setBizModel("demo");
        processService.approval(approvalInfo2DTO);
        return AjaxResult.success();
    }

    @PostMapping("/rollback")
    public AjaxResult rollback(@RequestBody ApprovalInfo2DTO approvalInfo2DTO) {
        Map<String, Object> variables = new HashMap<String, Object>();
        approvalInfo2DTO.setBizModel("demo");
        processService.rollback(approvalInfo2DTO);
        return AjaxResult.success();
    }

    @GetMapping(value = "/progress")
    public TableDataInfo<ApprovalProgressVO> progress(@RequestParam("bizId")Long bizId) {
        Workflow workflow = workflowService.getOne("demo",bizId);
        List<ApprovalProgressVO> list = processQueryService.selectApprovalProgressList(workflow.getProcInstId());
        return getDataTable(list);
    }


}
