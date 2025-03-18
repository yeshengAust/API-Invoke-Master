package com.yes.common.config.security;

import com.alibaba.fastjson.JSON;
import com.yes.common.constants.SystemConstants;
import com.yes.common.enums.ErrorCode;
import com.yes.common.utils.JwtUtil;
import com.yes.common.utils.RedisCache;
import com.yes.common.utils.ResponseResult;
import com.yes.common.utils.WebUtils;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 放入SpringSecurity进行操作拦截
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String token = request.getHeader(SystemConstants.AUTHORIZATION);
        //判断该请求是否有token，如果没有token则放行
        if(Objects.isNull(token)){
            filterChain.doFilter(request,response);
            return;
        }
        //解析token获取userId
        Claims claims;
        try {
           claims = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            //因为拦截器尚未到达controller所以不能走全局异常处理，所以要在response中设置提示信息
            ResponseResult result =ResponseResult.errorResult(ErrorCode.NEED_LOGIN);
            //没有springmvc帮我们进行转json所以自己转
            WebUtils.renderString(response,JSON.toJSONString(result));
            //要终止因为可能用户过期，或者黑客篡改
            return;
        }
        String userId = claims.getSubject();
        //通过userId查询redis获取用户信息
        String key = SystemConstants.USER_CACHE + userId;

        UserLogin userLogin = redisCache.getCacheObject(key);
        //如果redis没有用户信息则返回
        if(Objects.isNull(userLogin)){
            ResponseResult result =ResponseResult.errorResult(ErrorCode.NEED_LOGIN);
            //没有springmvc帮我们进行转json所以自己转
            WebUtils.renderString(response,JSON.toJSONString(result));
            //要终止因为可能用户过期，或者黑客篡改
            return;
        }
        //将用户信息封装到SpringSecurityContext中
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userLogin,null,null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //更新redis
        redisCache.setCacheObject(SystemConstants.USER_CACHE+userLogin.getUser().getId(),userLogin,SystemConstants.USER_INFO_EXPIRE_TIME,SystemConstants.USER_INFO_EXPIRE_UNIT);
        //放行
        filterChain.doFilter(request, response);

    }


}