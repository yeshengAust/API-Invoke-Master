package com.yes.interfaces.utils;

import com.yes.sdk.client.ApiClient;
import lombok.Data;

@Data
public class ApiInvokeUtil {
    private String accessKey = "yesheng";
    private String secretKey = "75e0f27b639394e2e2cb9efc3fdf5004";


    public  String getName(String name) {
        ApiClient apiClient = new ApiClient(accessKey, secretKey);
        return apiClient.getName(name);
    }

    public static void main(String[] args) {
        String name = new ApiInvokeUtil().getName("hello");
        System.out.println(name);
    }
}



