package com.ruoyi.activiti.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.activiti.mapper.WorkflowMapper;
import com.ruoyi.activiti.domain.Workflow;
import com.ruoyi.activiti.service.IWorkflowService;
import com.ruoyi.activiti.domain.dto.QueryWorkflowDTO;

/**
 * 业务流程Service业务层处理
 *
 * @author ruoyi
 * @date 2024-08-21
 */
@Service
public class WorkflowServiceImpl extends ServiceImpl<WorkflowMapper, Workflow> implements IWorkflowService {

    /**
     * 查询业务流程列表
     *
     * @param queryWorkflowDTO 查询条件
     * @return 查询列表
     */
    @Override
    public List<Workflow> list(QueryWorkflowDTO queryWorkflowDTO) {
        return this.baseMapper.selectWorkflowList(queryWorkflowDTO);
    }

    @Override
    public boolean checkExistedWorkflow(String bizModel, Long bizId) {
        QueryWrapper<Workflow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_del", 0)
                    .eq("biz_model", bizModel)
                    .eq("biz_id", bizId)
                    .isNotNull("proc_inst_id");
        return this.exists(queryWrapper);
    }

    @Override
    public Workflow getOne(String processInstanceId) {
        QueryWrapper<Workflow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_del", 0).eq("proc_inst_id", processInstanceId);
        return this.getOne(queryWrapper);
    }

    @Override
    public Workflow getOne(String bizModel, Long bizId) {
        QueryWrapper<Workflow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_del", 0).eq("biz_model", bizModel).eq("biz_id", bizId);
        return this.getOne(queryWrapper);
    }

    @Override
    public Integer selectCountByParam(List<Integer> flowStatusList, Long approverId, Integer dataType,
        String bizModel) {
        return baseMapper.selectCountByParam(flowStatusList, approverId, dataType, bizModel);
    }

}
