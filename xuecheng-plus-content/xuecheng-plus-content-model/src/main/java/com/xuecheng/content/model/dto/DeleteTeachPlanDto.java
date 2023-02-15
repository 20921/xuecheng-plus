package com.xuecheng.content.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteTeachPlanDto implements Serializable {

    private String errCode;
    private String errMessage;


    /**
     * 删除成功
     *
     * @return
     */
    public static DeleteTeachPlanDto sucess() {
        DeleteTeachPlanDto deleteTeachPlanDto = new DeleteTeachPlanDto();
        deleteTeachPlanDto.setErrCode("200");
        return deleteTeachPlanDto;
    }

    /**
     * 删除失败
     *
     * @return
     */
    public static DeleteTeachPlanDto err() {
        DeleteTeachPlanDto deleteTeachPlanDto = new DeleteTeachPlanDto();
        deleteTeachPlanDto.setErrCode("120409");
        deleteTeachPlanDto.setErrMessage("课程计划信息还有子级信息，无法操作");
        return deleteTeachPlanDto;
    }
}
