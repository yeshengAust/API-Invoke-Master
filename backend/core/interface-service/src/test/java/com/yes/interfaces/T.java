package com.yes.interfaces;

import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import com.yes.common.constants.SystemConstants;
import com.yes.common.utils.RedisCache;
import com.yes.interfaces.mapper.InterfaceInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
public class T {
    @Autowired
    RedisCache redisCache;
    @Autowired
    InterfaceInfoMapper mapper;

    @Test
    public void test() {
        Map<String, Object> cacheMap = redisCache.getCacheMap(SystemConstants.INVOKE_COUNT_PREFIX);
        // 使用 Stream API 将键和值转换为 Long 类型
        Map<Long, Long> longMap = cacheMap.entrySet().stream()
                .filter(entry -> canConvertToLong(entry.getKey()) && canConvertToLong(entry.getValue().toString()))
                .collect(Collectors.toMap(
                        entry -> Long.valueOf(entry.getKey()),
                        entry -> Long.valueOf(entry.getValue().toString())
                ));
        System.out.println(longMap);
        mapper.updateInvokeCountBatch(longMap);

    }
    public static Map<Long, Long> convertToInvokeCountMap(List<Map<String, Object>> resultList) {
        return resultList.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row.get("id"),
                        row -> (Long) row.get("invoke_count"),
                        (existing, replacement) -> existing,
                        HashMap::new
                ));
    }
    private static boolean canConvertToLong(String str) {
        try {
            Long.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
