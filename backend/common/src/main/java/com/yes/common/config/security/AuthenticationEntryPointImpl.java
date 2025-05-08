package com.yes.common.config.security;

import com.alibaba.fastjson.JSON;

import com.yes.common.enums.ErrorCode;
import com.yes.common.utils.ResponseResult;
import com.yes.common.utils.WebUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        ResponseResult result;

        //判断异常的类型
        if(e instanceof BadCredentialsException){
            result = ResponseResult.errorResult(ErrorCode.ACCOUNT_OR_PASSWORD_ERROR.getCode(),e.getMessage());
        }
        else if(e instanceof InsufficientAuthenticationException) {
            result = ResponseResult.errorResult(ErrorCode.NEED_LOGIN);
        }
        else {
            result = ResponseResult.errorResult(ErrorCode.AUTHENTICATE_OR_AUTHORIZE_FAIL);
        }

        //封装返回参数

        WebUtils.renderString(httpServletResponse, JSON.toJSONString(result));
    }
}
