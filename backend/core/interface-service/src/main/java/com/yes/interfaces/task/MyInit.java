package com.yes.interfaces.task;

import com.yes.common.constants.SystemConstants;
import com.yes.common.utils.RedisCache;
import com.yes.interfaces.mapper.InterfaceInfoMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class MyInit {
    private final InterfaceInfoMapper interfaceInfoMapper;
    private final RedisCache redisCache;
    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void asyncInit() {
        List<Map<String, Object>> resultList = interfaceInfoMapper.getAllInterfaceInvokeCount();
        Map<Long, Long> invokeCountMap = convertToInvokeCountMap(resultList);
        Map<String, Integer> counts = invokeCountMap.entrySet().stream().collect(Collectors.toMap(entry -> String.valueOf(entry.getKey()), entry ->  entry.getValue().intValue()));
        redisCache.setCacheMap(SystemConstants.INVOKE_COUNT_PREFIX,counts);
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
}
