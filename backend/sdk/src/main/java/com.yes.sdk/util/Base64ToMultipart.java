package com.yes.sdk.util;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.util.Base64;

public class Base64ToMultipart {
    public static HttpEntity getMultipartFile(String base){
        byte[] decodedBytes = Base64.getDecoder().decode(base);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", decodedBytes, ContentType.IMAGE_PNG, "example.png");
        HttpEntity multipart = builder.build();
        return multipart;
    }
}
