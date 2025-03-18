package com.yes.interfaces.exception;



import com.yes.common.utils.ResponseResult;
import com.yes.sdk.exception.ApiInvokeException;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.SystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(ApiInvokeException.class)
    public ResponseResult apiExceptionHandler(ApiInvokeException e){
        //打印异常信息
        log.info("出现了异常！ {}",e);
        //从异常对象中获取提示信息封装返回
        return ResponseResult.errorResult(e.getCode(),e.getMsg());
    }

}
