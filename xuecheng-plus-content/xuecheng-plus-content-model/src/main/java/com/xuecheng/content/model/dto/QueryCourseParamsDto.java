package com.xuecheng.content.model.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class QueryCourseParamsDto {

//    @ApiModelProperty("审核状态")
    private String auditStatus;//审核状态
//    @ApiModelProperty("课程名称")
    private String courseName;//课程名称
//    @ApiModelProperty("发布状态")
    private String publishStatus;//发布状态
}
