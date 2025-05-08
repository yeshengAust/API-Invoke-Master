package com.yes.user.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.http.protocol.HttpDateGenerator;

import java.util.Date;

@Data
@Accessors(chain = true)
public class UserInfoVo {
    /**
     * 主键
     */
    private Long id;

    /**
     * 昵称
     */
    private String username;

    /**
     * 头像
     */
    private String avatar;

    private Integer gender;

    private String email;
    private Integer status;
    private Date  updateTime;
    private Date  createTime;


}