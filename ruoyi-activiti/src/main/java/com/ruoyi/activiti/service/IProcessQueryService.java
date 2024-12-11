package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.vo.ApprovalProgressVO;
import com.ruoyi.activiti.domain.vo.ApprovalRecordGroupVO;
import com.ruoyi.activiti.domain.vo.ApprovalRecordVO;

import java.util.List;

/**
 * 流程处理Service接口
 *
 * @author jackyfan
 * @date 2024-07-05
 */
public interface IProcessQueryService {
    public List<ApprovalRecordVO> selectApprovedTaskList(String processInstanceId);

    public List<ApprovalRecordVO> selectApprovedTaskListWithAttachment(String processInstanceId);

    public List<ApprovalProgressVO> selectApprovalProgressList(String processInstanceId);

    public List<ApprovalRecordGroupVO> selectApprovedRecords(String processInstanceId);

}
