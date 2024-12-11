package com.ruoyi.activiti.controller;

import cn.hutool.core.lang.Assert;
import com.ruoyi.activiti.domain.Workflow;
import com.ruoyi.activiti.domain.vo.ApprovalProgressVO;
import com.ruoyi.activiti.domain.vo.ApprovalRecordGroupVO;
import com.ruoyi.activiti.service.IProcessQueryService;
import com.ruoyi.activiti.service.IWorkflowService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * 业务流程Controller
 *
 * @author ruoyi
 * @date 2024-08-21
 */
@Api(tags = "业务流程")
@RestController
@RequestMapping("/activiti/workflow")
public class WorkflowController extends BaseController {
    @Autowired
    private IWorkflowService workflowService;

    @Autowired
    private IProcessQueryService processQueryService;


    @ApiOperation(value = "审批进度")
    @GetMapping(value = "/approval/progress")
    public TableDataInfo<ApprovalProgressVO> progress(@ApiParam(value = "业务model", required = true) @RequestParam String bizModel,
                                                      @ApiParam(value = "业务Id", required = true) @RequestParam Long bizId) {
        Workflow workflow = Optional.ofNullable(workflowService.getOne(bizModel, bizId))
                                    .orElse(new Workflow());
        Assert.notNull(workflow.getProcInstId(), "流程实例不存在");
        List<ApprovalProgressVO> list = processQueryService.selectApprovalProgressList(workflow.getProcInstId());
        return getDataTable(list);
    }

    @ApiOperation(value = "审批记录")
    @GetMapping(value = "/approval/record")
    public TableDataInfo<ApprovalRecordGroupVO> record(@ApiParam(value = "业务model", required = true) @RequestParam String bizModel,
                                                       @ApiParam(value = "业务Id", required = true) @RequestParam Long bizId) {
        Workflow workflow = Optional.ofNullable(workflowService.getOne(bizModel, bizId))
                                    .orElse(new Workflow());
        Assert.notNull(workflow.getProcInstId(), "流程实例不存在");
        List<ApprovalRecordGroupVO> list = processQueryService.selectApprovedRecords(workflow.getProcInstId());
        return getDataTable(list);
    }

}
