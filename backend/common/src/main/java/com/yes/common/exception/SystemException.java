package com.yes.common.exception;



import com.yes.common.enums.ErrorCode;
import lombok.Getter;

@Getter
public class SystemException extends RuntimeException{
    private int code;
    private String msg;
    public SystemException(ErrorCode httpCodeEnum) {
        super(httpCodeEnum.getMessage());
        this.code = httpCodeEnum.getCode();
        this.msg = httpCodeEnum.getMessage();
    }
    public SystemException(String msg) {
        super(msg);
        code = 500;
        this.msg = msg;
    }
    
}