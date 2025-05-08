package com.yes.gateway.utils;

import org.apache.dubbo.common.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

public class InterceptorUtils {

    public static String getParams(HttpServletRequest request) throws Exception {
            // 获取请求方法
            String method = request.getMethod();
            String res="";
            if ("GET".equalsIgnoreCase(method)) {
                // 处理GET请求参数
                res =  handleGetParameters(request);
            } else if ("POST".equalsIgnoreCase(method)) {
                // 处理POST请求参数
                res= handlePostParameters(request);
            }
            return res;

    }

    public static String handleGetParameters(HttpServletRequest request) {
        // 获取所有参数名
        ArrayList<String> paramList = new ArrayList<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            String param = "'"+paramName+"':"+"'"+paramValue+"'";
            paramList.add(param);
            System.out.println("GET Parameter: " + paramName + " = " + paramValue);
        }
        String[] paramArr = new String[paramList.size()];
        paramList.toArray(paramArr);
        return "{" + StringUtils.join(paramArr ,",") + "}";
    }

    public  static String handlePostParameters(HttpServletRequest request) throws IOException {
        String contentType = request.getContentType();
        ArrayList<String> paramList = new ArrayList<>();
        if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
            // 处理表单数据
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                String paramValue = request.getParameter(paramName);
                String param = "'"+paramName+"':"+"'"+paramValue+"'";
                paramList.add(param);
                System.out.println("POST Form Parameter: " + paramName + " = " + paramValue);
            }
            String[] paramArr = new String[paramList.size()];
            paramList.toArray(paramArr);
            return "{" + StringUtils.join(paramArr ,",") + "}";

        } else if (contentType != null && contentType.contains("application/json")) {
            // 处理JSON数据
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String jsonPayload = sb.toString();
            return jsonPayload;
        }
        else {
            return "内容无法解析";
        }
    }
}
