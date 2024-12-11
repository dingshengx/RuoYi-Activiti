package com.ruoyi.activiti.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "流程附件")
public class AttachmentVO {

    @ApiModelProperty(value = "附件ID")
    private String fileId;

    @ApiModelProperty(value = "附件名")
    private String fileName;

    //@ApiModelProperty(value = "附件描述")
    private String description;

    //@ApiModelProperty(value = "附件类型")
    private String type;

    private String taskId;


}
