package com.ruoyi.activiti.mapper;

import com.ruoyi.activiti.domain.vo.ActRuTaskVO;
import com.ruoyi.activiti.domain.vo.ApprovalRecordVO;
import com.ruoyi.activiti.domain.vo.AttachmentVO;

import java.util.List;

/**
 * 查询activiti代办相关信息
 *
 * @author dingsheng
 * @date 2024/7/2
 */
public interface ActRuTaskMapper {

    /**
     * 根据流程实例id查询代办信息
     *
     * @param processInstanceId 流程实例id
     * @return 代办相关信息
     */
    public List<ActRuTaskVO> selectActRuTaskVo(String processInstanceId);

    /**
     * 根据流程实例id集合查询代办信息
     *
     * @param processInstanceIdList 流程实例id集合
     * @return 代办相关信息
     */
    public List<ActRuTaskVO> selectActRuTaskVoList(List<String> processInstanceIdList);

    /**
     * 查询审批记录
     * @param processInstanceId 流程实例
     * @return 审批记录
     */
    public List<ApprovalRecordVO> selectApprovedTaskList(String processInstanceId);

    /**
     * 查询流程附件
     * @param processInstanceId 流程实例
     * @return 审批记录
     */
    public List<AttachmentVO> selectAttachmentList(String processInstanceId);

}
