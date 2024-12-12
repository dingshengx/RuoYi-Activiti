package com.ruoyi.activiti.controller;

import com.ruoyi.activiti.constants.ProcessConstants;
import com.ruoyi.activiti.domain.Workflow;
import com.ruoyi.activiti.domain.dto.*;
import com.ruoyi.activiti.domain.vo.ApprovalProgressVO;
import com.ruoyi.activiti.service.IProcessQueryService;
import com.ruoyi.activiti.service.IProcessService;
import com.ruoyi.activiti.service.IWorkflowService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.activiti.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "流程DEMO")
@RestController
@RequestMapping("/activiti/demo")
public class DemoController extends BaseController {
  @Autowired private IProcessService processService;
  @Autowired private IProcessQueryService processQueryService;
  @Autowired private IWorkflowService workflowService;

  @PostMapping("/{processDefinitionKey}/submit")
  public AjaxResult submit(@PathVariable("processDefinitionKey") String processDefinitionKey) {
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

  @PostMapping("/approval")
  public AjaxResult approval(@RequestBody ApprovalInfoDTO approvalInfoDTO) {
    Map<String, Object> variables = new HashMap<String, Object>();
    approvalInfoDTO.setBizModel("demo");
    processService.approval(approvalInfoDTO);
    return AjaxResult.success();
  }

  @PostMapping("/rollback")
  public AjaxResult rollback(@RequestBody ApprovalInfoDTO approvalInfoDTO) {
    Map<String, Object> variables = new HashMap<String, Object>();
    approvalInfoDTO.setBizModel("demo");
    processService.rollback(approvalInfoDTO);
    return AjaxResult.success();
  }

  @GetMapping(value = "/progress")
  public TableDataInfo<ApprovalProgressVO> progress(@RequestParam("bizId") Long bizId) {
    Workflow workflow = workflowService.getOne("demo", bizId);
    List<ApprovalProgressVO> list =
        processQueryService.selectApprovalProgressList(workflow.getProcInstId());
    return getDataTable(list);
  }
}
