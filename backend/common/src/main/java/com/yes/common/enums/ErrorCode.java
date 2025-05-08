package com.yes.common.enums;

import lombok.Getter;

/**
 * 错误信息枚举类
 *
 * @author by
 */
@Getter
public enum ErrorCode {

    /**
     * 基本错误信息枚举
     */
    SUCCESS(200,"操作成功"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    NEED_LOGIN(401,"需要登录后操作"),
    NO_OPERATOR_AUTH(403,"无权限操作"),
    EMAIL_EXIST(503, "邮箱已存在"),
    REQUIRE_ACCOUNT(504, "必需填写用户名"),
    AUTHENTICATE_OR_AUTHORIZE_FAIL(503,"认证或授权失败"),
    ACCOUNT_PASSWORD_NOT_NULL (506,"用户名或者密码不能为空"),
    ARTICLE_NOT_EXISTS(507,"文章不存在"),
    CONTENT_NOT_NULL(508,"文章内容不能为空"),
    FILE_TYPE_ERROR(509,"文件上传错误"),
    ACCOUNT_NOT_NULL(508, "用户名不能为空"),
    NICKNAME_NOT_NULL(509, "昵称不能为空"),
    PASSWORD_NOT_NULL(510, "密码不能为空"),
    EMAIL_NOT_NULL(511, "邮箱不能为空"),
    ACCOUNT_NOT_EXIST(520,"用户不存在"),

    PASSWORD_ERROR(521,"密码错误"),
    ACCOUNT_OR_PASSWORD_ERROR(522,"用户名密码错误"),
    OLD_PASSWORD_ERROR(523,"旧密码错误"),
    REMAIN_NOT_ENOUGH(524,"调用次数不足，请购买"),

    AUTHENTICATED_ERROR(525,"令牌或秘钥错误"),
    VERIFY_CODE_ERROR(526,"验证码错误"),
    CREDIT_ERROR(527,"令牌不存在"),
    TIMEOUT(528,"请求超时"),
    INTERFACE_NOT_EXISTS(529,"接口不存在"),
    CREDIT_BALANCE_NOT_ENOUGH(530,"令牌余额不足请充值"),
    PRODUCT_NOT_EXIST(531,"商品不存在"),
    WALLET_NOT_ENOUGH(532,"余额不足，请充值"),
    PRODUCT_NOT_ENOUGH(533,"商品不足"),
    QUOTA_NOT_ENOUGH(534,"额度不足，请充值"),
    ACCOUNT_ALREADY_EXISTS(535,"账户已存在"),
    UPLOAD_AVATAR_NOT_NULL(536,"上传头像不能为空"),
    FILE_SORT_ERROR(537,"文件类型错误 "),
    OPTIONS_TOO_QUICK(538,"操作过于频繁"),

    SYSTEM_ERROR(50000, "系统内部异常");




    /**
     * 状态码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
