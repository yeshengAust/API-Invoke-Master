package com.yes.sdk.enums;

import lombok.Getter;

@Getter
public enum ApiInvokeError {
    CREDIT_ERROR(527,"令牌不存在"),
    TIMEOUT(528,"请求超时"),
    INTERFACE_NOT_EXISTS(529,"接口不存在"),
    CREDIT_BALANCE_NOT_ENOUGH(530,"令牌余额不足请充值");
    /**
     * 状态码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String message;

    ApiInvokeError(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
