package com.ruoyi.activiti.controller;

import cn.hutool.core.lang.Assert;
import com.ruoyi.activiti.constants.ProcessConstants;
import com.ruoyi.activiti.domain.Workflow;
import com.ruoyi.activiti.domain.dto.ApprovalInfoDTO;
import com.ruoyi.activiti.domain.dto.StartWorkflowDTO;
import com.ruoyi.activiti.domain.vo.ApprovalProgressVO;
import com.ruoyi.activiti.domain.vo.ApprovalRecordGroupVO;
import com.ruoyi.activiti.service.IProcessQueryService;
import com.ruoyi.activiti.service.IProcessService;
import com.ruoyi.activiti.service.IWorkflowService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "流程业务")
@RestController
@RequestMapping("/activiti/workflow")
public class WorkflowController extends BaseController {
  @Autowired private IWorkflowService workflowService;
  @Autowired private IProcessQueryService processQueryService;
  @Autowired private IProcessService processService;

  @PostMapping("/demo/{processDefinitionKey}/submit")
  public AjaxResult<String> submit(
      @PathVariable("processDefinitionKey") String processDefinitionKey) {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put(ProcessConstants.APPLICANT_ASSIGNEE_VARIABLE, this.getUsername());
    List<String> purchasingList = Arrays.asList("huishen1", "huishen2", "huishen3");
    variables.put("purchasingList", purchasingList);
    variables.put("operationDirector", "operationDirector");
    variables.put("leader", "operationDirector");
    variables.put("_ACTIVITI_SKIP_EXPRESSION_ENABLED", true); // 开启表达式自动跳过功能
    long rangeRandomLong = ThreadLocalRandom.current().nextLong(0, 1000000L);
    StartWorkflowDTO startWorkflowDTO =
        StartWorkflowDTO.builder()
            .processDefinitionKey(processDefinitionKey)
            .bizModel("demo")
            .bizId(rangeRandomLong)
            .variables(variables)
            .build();
    return AjaxResult.success(processService.submit(startWorkflowDTO));
  }

  @PostMapping("/demo/approval")
  public AjaxResult<Void> approval(@RequestBody ApprovalInfoDTO approvalInfoDTO) {
    Map<String, Object> variables = new HashMap<String, Object>();
    approvalInfoDTO.setBizModel("demo");
    processService.approval(approvalInfoDTO);
    return AjaxResult.success();
  }

  @ApiOperation(value = "审批进度")
  @GetMapping(value = "/approval/progress")
  public TableDataInfo<ApprovalProgressVO> progress(
      @ApiParam(value = "业务model", required = true) @RequestParam String bizModel,
      @ApiParam(value = "业务Id", required = true) @RequestParam Long bizId) {
    Workflow workflow =
        Optional.ofNullable(workflowService.getOne(bizModel, bizId)).orElse(new Workflow());
    Assert.notNull(workflow.getProcInstId(), "流程实例不存在");
    List<ApprovalProgressVO> list =
        processQueryService.selectApprovalProgressList(workflow.getProcInstId());
    return getDataTable(list);
  }

  @ApiOperation(value = "审批记录")
  @GetMapping(value = "/approval/record")
  public TableDataInfo<ApprovalRecordGroupVO> record(
      @ApiParam(value = "业务model", required = true) @RequestParam String bizModel,
      @ApiParam(value = "业务Id", required = true) @RequestParam Long bizId) {
    Workflow workflow =
        Optional.ofNullable(workflowService.getOne(bizModel, bizId)).orElse(new Workflow());
    Assert.notNull(workflow.getProcInstId(), "流程实例不存在");
    List<ApprovalRecordGroupVO> list =
        processQueryService.selectApprovedRecords(workflow.getProcInstId());
    return getDataTable(list);
  }
}
