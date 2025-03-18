package com.yes.user.constants;

import java.util.concurrent.TimeUnit;

/**
 * 常用常量
 */
public class UserConstants
{
    /**
     * 用户信息缓存前缀
     */
    public static final String USER_CACHE="cache:user:";
    /**
     * 登录验证码缓存前缀
     */
    public static final String LOGIN_USER_CODE_CACHE="cache:user:code:";
    /**
     * 注册邮箱验证码缓存前缀
     */
    public static final String REGISTRY_EMAIL_CODE_CACHE="cache:email:code:";
    /**
     * 普通用户的roleId
     */
    public static final Long USER_ROLE_ID = 2L;
    /**
     * 管理员的roleId
     */
    public static final Long ADMIN_ROLE_ID = 1L;
    /**
     * 用户信息缓存时间
     */
    public static final Integer USER_INFO_EXPIRE_TIME=10;
    /**
     * 缓存时间单位
     */
    public static final TimeUnit USER_INFO_EXPIRE_UNIT=TimeUnit.MINUTES;





}