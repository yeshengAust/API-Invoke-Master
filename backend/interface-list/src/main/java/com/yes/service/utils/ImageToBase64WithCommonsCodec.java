package com.yes.service.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageToBase64WithCommonsCodec {
    public static String imageToBase64(String filePath) {
        File file = new File(filePath);
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            fis.read(bytes);
            // 使用 Apache Commons Codec 进行编码
            return Base64.encodeBase64String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void convertBase64ToImage(String base64Str, String outputPath) {
        try {
            // 去除可能存在的 Base64 前缀，如 "data:image/jpeg;base64,"
            if (base64Str.contains(",")) {
                base64Str = base64Str.split(",")[1];
            }

            // 使用 Apache Commons Codec 进行 Base64 解码
            byte[] imageBytes = Base64.decodeBase64(base64Str);

            // 将解码后的字节数据写入文件
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                fos.write(imageBytes);
                System.out.println("图片已成功保存到: " + outputPath);
            }
        } catch (IOException e) {
            System.err.println("在将 Base64 转换为图片时出现 IO 异常: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("在将 Base64 转换为图片时出现异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String filePath = "E:\\apiItem\\vue-api\\api\\test\\src\\main\\java\\com\\yes\\test\\ee.png";
        String base64 = imageToBase64(filePath);
        if (base64 != null) {
            System.out.println("Base64 编码: " + base64);
        }
        convertBase64ToImage("iVBORw0KGgoAAAANSUhEUgAAAlgAAAJYCAYAAAC+ZpjcAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1B...","./out.png");
    }
}