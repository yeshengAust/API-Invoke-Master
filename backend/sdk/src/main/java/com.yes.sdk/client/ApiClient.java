package com.yes.sdk.client;


import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.sun.org.apache.xpath.internal.operations.Mult;
import com.yes.sdk.enums.ApiInvokeError;
import com.yes.sdk.exception.ApiInvokeException;
import com.yes.sdk.util.Base64ToMultipart;
import com.yes.sdk.util.SignUtil;
import lombok.Data;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


import java.util.HashMap;
import java.util.Map;

/**
 * sdk客户端
 *
 * @author by
 */



public class ApiClient {

    /**
     * 签名标识
     */
    private final String accessKey;
    /**
     * 密钥
     */
    private final String secretKey;

    /**
     * 网关地址
     */
    private static final String GATEWAY_HOST = "http://localhost:9090";

    /**
     * 加密钥匙
     */
    public static final String BODY_KEY = "body-key";

    /**
     * 统一访问前缀
     *
     * @param accessKey
     * @param secretKey
     */
    public static final String prefix = GATEWAY_HOST + "/api/apiService/";

    public ApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * 获取输入的名称
     *
     * @param name 名称
     * @return 名称
     */
    public String getName(String name) {
        //添加请求头
        Map<String, String> headerMap = this.getHeaderMap(BODY_KEY);
        String url = prefix + "getName";
        //发送请求
        HttpRequest httpRequest = HttpRequest.get(url)
                .form("name", name)
                .addHeaders(headerMap);
        //返回请求数据
        @Data
        class User {
            String name;
        }
        HttpResponse execute = httpRequest.execute();
        checkError(execute);
        return execute.body();
    }

    public void checkError(HttpResponse response) {
        Integer code = response.getStatus();
        if (code == 527) {
            throw new ApiInvokeException(ApiInvokeError.CREDIT_ERROR);
        } else if (code == 528) {
            throw new ApiInvokeException(ApiInvokeError.TIMEOUT);
        } else if (code == 529) {
            throw new ApiInvokeException(ApiInvokeError.INTERFACE_NOT_EXISTS);
        } else if (code == 530) {
            throw new ApiInvokeException(ApiInvokeError.CREDIT_BALANCE_NOT_ENOUGH);
        }
    }

    public String cartoonImage(String base64Data) {


        Map<String, String> headerMap = this.getHeaderMap(BODY_KEY);
        String url = prefix + "cartoonImage";
        //发送请求
        HttpRequest httpRequest = HttpRequest.post(url)
                .form("base64Data", base64Data)
                .addHeaders(headerMap);
        //返回请求数据
        HttpResponse execute = httpRequest.execute();
        checkError(execute);

        String res = execute.body().replace('"', ' ').trim();

        return res;
    }

    /**
     * 随机生成图片
     *
     * @return 图片地址
     */
    public String randomImageUrl() {
        //添加请求头
        Map<String, String> headerMap = this.getHeaderMap(BODY_KEY);
        //发送请求
        HttpRequest httpRequest = HttpRequest.get(GATEWAY_HOST + "/actual/random/imageUrl")
                .addHeaders(headerMap);
        //返回请求数据
        return httpRequest.execute().body();
    }

    /**
     * 随机生成土味情话
     *
     * @return 土味情话
     */
    public String randomLoveTalk() {
        //添加请求头
        Map<String, String> headerMap = this.getHeaderMap(BODY_KEY);
        //发送请求
        HttpRequest httpRequest = HttpRequest.get(GATEWAY_HOST + "/actual/random/loveTalk")
                .addHeaders(headerMap);
        //返回请求数据
        return httpRequest.execute().body();
    }


    public Map<String, String> getHeaderMap(String bodyKey) {
        Map<String, String> map = new HashMap<>(3);
        //凭证
        map.put("accessKey", accessKey);
        map.put("secretKey", secretKey);
        //签名
        //随机数和时间戳用于防止请求重放
        //添加随机数
        map.put("nonce", RandomUtil.randomNumbers(4));
        //添加时间戳
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return map;
    }
}
