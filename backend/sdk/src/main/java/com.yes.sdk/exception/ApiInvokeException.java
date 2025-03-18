package com.yes.sdk.exception;


import com.yes.sdk.enums.ApiInvokeError;
import lombok.Getter;

@Getter
public class ApiInvokeException extends RuntimeException{
    private int code;
    private String msg;
    public ApiInvokeException(ApiInvokeError error) {
        super(error.getMessage());
        this.code = error.getCode();
        this.msg = error.getMessage();
    }
    
}