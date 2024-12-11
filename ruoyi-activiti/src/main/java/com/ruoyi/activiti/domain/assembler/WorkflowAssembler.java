package com.ruoyi.activiti.domain.assembler;

import java.util.ArrayList;
import java.util.List;
import com.github.pagehelper.Page;
import org.springframework.beans.BeanUtils;
import com.ruoyi.activiti.domain.Workflow;
import com.ruoyi.activiti.domain.dto.WorkflowDTO;
import com.ruoyi.activiti.domain.vo.WorkflowVO;

/**
 * 业务流程 Assembler
 * 
 * @author ruoyi
 * @date 2024-08-21
 */
public class WorkflowAssembler {

    /**
     * 业务流程Entity TO DTO
     *
     * @param  workflow
     * @return workflowDTO
     */
    public static WorkflowDTO toDTO(Workflow workflow) {
        if (workflow == null) {
            return null;
        }
        WorkflowDTO workflowDTO = new WorkflowDTO();
        BeanUtils.copyProperties(workflow, workflowDTO);
        return workflowDTO;
    }

    /**
     * 业务流程DTO TO Entity
     *
     * @param  workflowDTO
     * @return workflow
     */
    public static Workflow toEntity(WorkflowDTO workflowDTO) {
        if (workflowDTO == null) {
            return null;
        }
        Workflow workflow = new Workflow();
        BeanUtils.copyProperties(workflowDTO, workflow);
        return workflow;
    }

    /**
     * 业务流程Entity TO VO
     *
     * @param  workflow
     * @return workflowVO
     */
    public static WorkflowVO toVO(Workflow workflow) {
        if (workflow == null) {
            return null;
        }
        WorkflowVO workflowVO = new WorkflowVO();
        BeanUtils.copyProperties(workflow, workflowVO);
        return workflowVO;
    }

    /**
     * 业务流程Entity TO VO
     *
     * @param  workflows
     * @return workflowVOs
     */
    public static List<WorkflowVO> toVOs(List<Workflow> workflows) {
        if (workflows == null) {
            return null;
        }
        List<WorkflowVO> workflowVOs = new ArrayList<WorkflowVO>(workflows.size());
        for (Workflow entity : workflows) {
            workflowVOs.add(toVO(entity));
        }
        /*判断是否为“分页”对象，需要将分页信息返回*/
        if (workflows instanceof Page) {
            Page tempPage = (Page) workflows;
            tempPage.clear();
            tempPage.addAll(workflowVOs);
            return tempPage;
        }
        return workflowVOs;
    }
}
