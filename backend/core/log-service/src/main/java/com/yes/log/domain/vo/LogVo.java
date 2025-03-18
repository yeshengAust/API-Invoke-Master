package com.yes.log.domain.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @TableName log
 */
@TableName(value ="log")
@Data
public class LogVo {

    private Date createTime;

    private String creditSort;

    private String group;

    private String creditName;

    private Long useTime;
    private Long invokeTime;

    private Long cost;
    private String requestParams;
    private String response;

}