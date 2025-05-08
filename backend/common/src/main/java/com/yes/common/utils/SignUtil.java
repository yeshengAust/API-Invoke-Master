package com.yes.common.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

public class SignUtil {

    public static String generateSign(String body, String secretKey) {
        String newSecret = body + "-" + secretKey;
        Digester digester = new Digester(DigestAlgorithm.SHA256);
        return digester.digestHex(newSecret);
    }
}