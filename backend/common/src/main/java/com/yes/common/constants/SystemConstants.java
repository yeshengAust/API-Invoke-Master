package com.yes.common.constants;

import java.util.concurrent.TimeUnit;

public class SystemConstants {
    /**
     *token
     */
    public static final String AUTHORIZATION = "Authorization";
    public static final Long GROUP_BUY = 2L;
    /**
     * 文章是草稿
     */


    public static final String USER_CACHE = "cache:user:";


    /**
     * 验证码过期时间
     */
    public static final Integer VERIFY_EXPIRE_TIME = 30;

    /**
     * 注册验证码过期时间
     */
    public static final Integer REGISTRY_EXPIRE_TIME = 90;
    /**
     * 过期时间单位
     */
    public static final TimeUnit EXPIRE_UNIT = TimeUnit.SECONDS;

    /**
     * 热门商品 FLAG
     */
    public static final Integer IS_HOT = 1;
    /**
     * 用户信息缓存时间
     */
    public static final Integer USER_INFO_EXPIRE_TIME=10;
    /**
     * 缓存时间单位
     */
    public static final TimeUnit USER_INFO_EXPIRE_UNIT=TimeUnit.MINUTES;
    /**
     * 设置api超时调用
     */
    public Integer TIME_GAP = 10 * 60 * 1000;
    /**
     * 接口调用次数prefix
     */
    public static final String INVOKE_COUNT_PREFIX = "cache:invoke-count:";

}