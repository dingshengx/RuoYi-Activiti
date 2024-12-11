package com.ruoyi.activiti.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "流程附件")
public class AttachmentDTO {

    @ApiModelProperty(value = "附件ID",required = true)
    @NotBlank(message = "附件ID不能为空")
    private String fileId;

    @ApiModelProperty(value = "附件名",required = true)
    @NotBlank(message = "附件名不能为空")
    private String fileName;

    @ApiModelProperty(value = "附件描述")
    private String description;

    @ApiModelProperty(value = "附件类型")
    private String type;


}
