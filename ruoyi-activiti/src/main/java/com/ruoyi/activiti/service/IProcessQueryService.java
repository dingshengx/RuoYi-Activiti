package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.vo.ApprovalProgressVO;
import com.ruoyi.activiti.domain.vo.ApprovalRecordGroupVO;
import com.ruoyi.activiti.domain.vo.ApprovalRecordVO;
import java.util.List;

/**
 * 流程查询Service接口
 *
 * @author jackyfan
 * @date 2024-07-05
 */
public interface IProcessQueryService {
  public List<ApprovalRecordVO> selectApprovedTaskList(String processInstanceId);

  public List<ApprovalRecordVO> selectApprovedTaskListWithAttachment(String processInstanceId);

  /**
   * 查询审批进度
   *
   * @param processInstanceId 流程实例ID
   * @return 审批进度
   */
  public List<ApprovalProgressVO> selectApprovalProgressList(String processInstanceId);

  /**
   * 查询审批记录
   *
   * @param processInstanceId 流程实例ID
   * @return 审批记录
   */
  public List<ApprovalRecordGroupVO> selectApprovedRecords(String processInstanceId);
}
