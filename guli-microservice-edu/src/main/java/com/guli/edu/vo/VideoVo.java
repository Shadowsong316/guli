package com.guli.edu.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel(value = "节/视频信息")
public class VideoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String title;

    private Boolean free;

    @ApiModelProperty(value = "云服务器上存储的视频文件名称")
    private String videoOriginalName;
}
