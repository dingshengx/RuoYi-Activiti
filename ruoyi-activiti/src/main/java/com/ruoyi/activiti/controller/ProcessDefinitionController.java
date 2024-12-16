package com.ruoyi.activiti.controller;

import com.ruoyi.activiti.domain.dto.DefinitionIdDTO;
import com.ruoyi.activiti.domain.dto.ProcessDefinitionDTO;
import com.ruoyi.activiti.domain.dto.QueryProcessDefinitionDTO;
import com.ruoyi.activiti.domain.vo.ProcessDefinitionVO;
import com.ruoyi.activiti.service.IProcessDefinitionService;
import com.ruoyi.activiti.service.IProcessService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "流程定义")
@RestController
@RequestMapping("/processDefinition")
public class ProcessDefinitionController extends BaseController {

  @Autowired private IProcessDefinitionService processDefinitionService;
  @Autowired private IProcessService processService;

  /**
   * 获取流程定义集合
   *
   * @param processDefinition 入参
   * @return 流程定义集合
   */
  @ApiOperation(value = "列表")
  @GetMapping(value = "/list")
  public TableDataInfo<ProcessDefinitionVO> list(QueryProcessDefinitionDTO processDefinition) {
    startPage();
    List<ProcessDefinitionVO> list =
        processDefinitionService.selectProcessDefinitionList(processDefinition);
    return getDataTable(list);
  }

  /**
   * 获取流程定义详情
   *
   * @param instanceId 入参
   * @return 流程定义详情
   */
  @ApiOperation(value = "详情")
  @GetMapping(value = "/getDefinitions/{instanceId}")
  public AjaxResult<DefinitionIdDTO> getDefinitionsByInstanceId(
      @PathVariable("instanceId") String instanceId) {
    return AjaxResult.success(processDefinitionService.getDefinitionsByInstanceId(instanceId));
  }

  /**
   * 删除流程定义
   *
   * @param deploymentId 部署ID
   * @return 结果
   */
  @ApiOperation(value = "删除")
  @Log(title = "流程定义管理", businessType = BusinessType.DELETE)
  @DeleteMapping(value = "/remove/{deploymentId}")
  public AjaxResult<Integer> delDefinition(@PathVariable("deploymentId") String deploymentId) {
    return toAjax(processDefinitionService.deleteProcessDefinitionById(deploymentId));
  }

  /**
   * 启动挂起流程流程定义
   *
   * @param processDefinition 入参
   * @return 结果
   */
  @ApiOperation(value = "启动/挂起")
  @Log(title = "流程定义管理", businessType = BusinessType.UPDATE)
  @PostMapping("/suspendOrActiveApply")
  @ResponseBody
  public AjaxResult<Void> suspendOrActiveApply(
      @RequestBody ProcessDefinitionDTO processDefinition) {
    processDefinitionService.suspendOrActiveApply(
        processDefinition.getId(), processDefinition.getSuspendState());
    return AjaxResult.success();
  }

  /**
   * 上传并部署流程定义
   *
   * @param file 文件
   * @return 结果
   * @throws IOException io异常
   */
  @ApiOperation(value = "导入部署")
  @Log(title = "流程定义管理", businessType = BusinessType.IMPORT)
  @PostMapping(value = "/uploadStreamAndDeployment")
  public AjaxResult<Void> uploadStreamAndDeployment(@RequestParam("file") MultipartFile file)
      throws IOException {
    processDefinitionService.uploadStreamAndDeployment(file);
    return AjaxResult.success();
  }

  /**
   * 上传流程流程定义
   *
   * @param multipartFile multipart文件
   * @return 文件名
   * @throws IOException Io异常
   */
  @ApiOperation(value = "导入")
  @Log(title = "流程定义管理", businessType = BusinessType.IMPORT)
  @PostMapping(value = "/upload")
  public AjaxResult<String> upload(@RequestParam("processFile") MultipartFile multipartFile)
      throws IOException {

    if (!multipartFile.isEmpty()) {
      String fileName = processDefinitionService.upload(multipartFile);
      return AjaxResult.success("操作成功", fileName);
    }
    return AjaxResult.error("不允许上传空文件！");
  }

  /**
   * 通过stringBPMN添加流程定义
   *
   * @param stringBPMN stringBPMN
   * @return 结果
   */
  @ApiOperation(value = "stringBPMN部署")
  @PostMapping(value = "/addDeploymentByString")
  public AjaxResult<Void> addDeploymentByString(@RequestParam("stringBPMN") String stringBPMN) {
    processDefinitionService.addDeploymentByString(stringBPMN);
    return AjaxResult.success();
  }

  /**
   * 获取流程定义XML
   *
   * @param response response
   * @param deploymentId deploymentId
   * @param resourceName resourceName
   */
  @ApiOperation(value = "获取bpmn.xml")
  @GetMapping(value = "/getDefinitionXML")
  public void getProcessDefineXML(
      HttpServletResponse response,
      @RequestParam("deploymentId") String deploymentId,
      @RequestParam("resourceName") String resourceName)
      throws IOException {

    processDefinitionService.getProcessDefineXML(response, deploymentId, resourceName);
  }
}
