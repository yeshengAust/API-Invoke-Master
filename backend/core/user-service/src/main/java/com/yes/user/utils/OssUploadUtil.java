package com.yes.user.utils;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import com.qiniu.util.StringMap;
import com.yes.common.enums.ErrorCode;
import com.yes.common.exception.SystemException;
import com.yes.common.utils.PathUtils;
import com.yes.common.utils.ResponseResult;
import lombok.Data;
import org.json.JSONException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.json.JSONArray;
import org.json.JSONObject;

@Service
@Data
@ConfigurationProperties(prefix = "oss")
public class OssUploadUtil{

    public String uploadImg(MultipartFile img) {
        //判断文件类型
        //获取原始文件名
        String originalFilename = img.getOriginalFilename();
        //对原始文件名进行判断
        if(!originalFilename.endsWith(".png")&&!originalFilename.endsWith(".jpg")){
            throw new SystemException(ErrorCode.FILE_SORT_ERROR);
        }

        //如果判断通过上传文件到OSS
        String filePath = PathUtils.generateFilePath(originalFilename);
        String url = uploadOss(img,filePath);//  2099/2/3/wqeqeqe.png
        //将url存入到数据库中

        return url;
    }

    private String accessKey;
    private String secretKey;
    private String bucket;


    private String uploadOss(MultipartFile imgFile, String filePath){
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = filePath;
        try {
            InputStream inputStream = imgFile.getInputStream();
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            try {
                Response response = uploadManager.put(inputStream,key,upToken,null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                return getCDNDomainsByBucket(accessKey,secretKey,bucket)+key;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception ex) {
            //ignore
        }
        return "www";
    }
    public static String getCDNDomainsByBucket(String accessKey, String secretKey, String bucketName) {
        Auth auth = Auth.create(accessKey, secretKey);
        String url = "http://api.qiniu.com/v6/domain/list?tbl=" + bucketName;
        StringMap headers = new StringMap();
        headers.put("Authorization", "QBox " + auth.signRequest(url, null, "GET"));
        try {
            com.qiniu.http.Client client = new com.qiniu.http.Client();
            Response response = client.get(url, headers);
            if (response.isOK()) {
                JSONArray jsonArray = new JSONArray(response.bodyString());
                String[] domains = new String[jsonArray.length()];
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("http://");
                for (int i = 0; i < jsonArray.length(); i++) {
                   stringBuilder.append(jsonArray.getString(i));
                }
                stringBuilder.append("/");
            return stringBuilder.toString();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}