package com.ruoyi.activiti.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 流程定义 VO
 *
 * @author dingsheng
 * @date 2024/8/01
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流程定义")
public class ProcessDefinitionVO {

    @ApiModelProperty(value = "流程ID")
    private String id;

    @ApiModelProperty(value = "流程名称")
    private String name;

    @ApiModelProperty(value = "流程KEY")
    private String key;

    @ApiModelProperty(value = "版本")
    private int version;

    @ApiModelProperty(value = "部署ID")
    private String deploymentId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "部署时间")
    private Date deploymentTime;

    @ApiModelProperty(value = "流程实例状态 1 激活 2 挂起")
    private Integer suspendState;

    @ApiModelProperty(value = "流程文件名称")
    private String resourceName;

}
