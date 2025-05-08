package com.yes.buy.utils;

import com.yes.common.enums.ErrorCode;
import com.yes.common.exception.SystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
public class TokenBucketService {

    private static final String TOKEN_BUCKET_KEY_PREFIX = "token_bucket:";
    private static final String LAST_REFILL_TIME_KEY_SUFFIX = ":last_refill_time";
    private static final RedisScript<Long> TOKEN_BUCKET_SCRIPT;

    static {
        StringBuilder scriptContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("lua/token_bucket.lua").getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                scriptContent.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Lua script", e);
        }
        TOKEN_BUCKET_SCRIPT = new DefaultRedisScript<>(scriptContent.toString(), Long.class);
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public boolean tryAcquire(String bucketName, int capacity, double rate, int tokens) {
        String bucketKey = TOKEN_BUCKET_KEY_PREFIX + bucketName;
        String lastRefillTimeKey = bucketKey + LAST_REFILL_TIME_KEY_SUFFIX;
        List<String> keys = Arrays.asList(bucketKey, lastRefillTimeKey);
        long nowMillis = Instant.now().toEpochMilli();
        Long result = redisTemplate.execute(
                TOKEN_BUCKET_SCRIPT,
                keys,
                String.valueOf(capacity), // ARGV[1]
                String.valueOf(nowMillis), // ARGV[2]
                String.valueOf(nowMillis), // ARGV[3]，当前时间
                String.valueOf(rate), // ARGV[4]
                String.valueOf(tokens) // ARGV[5]
        );

        if (result == -1 || result == null) {
            // 处理参数错误的情况
            throw new SystemException(ErrorCode.SYSTEM_ERROR);
        }

        return result == 1;
    }
}