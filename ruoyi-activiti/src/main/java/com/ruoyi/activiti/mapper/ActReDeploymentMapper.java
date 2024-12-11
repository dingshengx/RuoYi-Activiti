package com.ruoyi.activiti.mapper;

import com.ruoyi.activiti.domain.dto.QueryProcessDefinitionDTO;
import com.ruoyi.activiti.domain.vo.ActReDeploymentVO;
import com.ruoyi.activiti.domain.vo.ProcessDefinitionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ActReDeploymentMapper {


        public List<ActReDeploymentVO> selectActReDeploymentByIds(@Param("ids") Set<String> ids);


        /**
         * 查询流程实例
         *
         * @param queryProcessDefinitionDTO 入参
         * @return 结果
         */
        public List<ProcessDefinitionVO> selectProcessDefinitionList(QueryProcessDefinitionDTO queryProcessDefinitionDTO);

}
