package com.ruoyi.activiti.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.activiti.domain.Workflow;
import com.ruoyi.activiti.domain.dto.QueryWorkflowDTO;
import org.apache.ibatis.annotations.Param;

/**
 * 业务流程Mapper接口
 * 
 * @author ruoyi
 * @date 2024-08-21
 */
public interface WorkflowMapper extends BaseMapper<Workflow> {

    /**
     * 查询业务流程列表
     *
     * @param  queryWorkflowDTO
     * @return 业务流程集合
     */
    public List<Workflow> selectWorkflowList(QueryWorkflowDTO queryWorkflowDTO);

    /**
     * 根据参数查询代办数量
     *
     * @param flowStatusList 状态List
     * @param approverId 待审批人
     * @param dataType 数据类型 0-自己 1-他人
     * @param bizModel 业务模块
     * @return 数量
     */
    public int selectCountByParam(@Param("flowStatusList") List<Integer> flowStatusList,
        @Param("approverId") long approverId, @Param("dataType") int dataType, @Param("bizModel") String bizModel);

}