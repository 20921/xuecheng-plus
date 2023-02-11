package com.xuecheng.base.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PageParams {

//    @ApiModelProperty("当前页")
    private Long pageNo = 1L; //当前页
//    @ApiModelProperty("默认分页")
    private Long pageSize = 10L;//默认分页

    public PageParams() {
    }

    public PageParams(long pageNo, long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
