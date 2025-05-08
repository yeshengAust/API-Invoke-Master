package com.yes.interfaces.task;

import com.yes.common.constants.SystemConstants;
import com.yes.common.utils.RedisCache;
import com.yes.interfaces.mapper.InterfaceInfoMapper;
import com.yes.interfaces.service.InterfaceInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MyScheduledTasks {
    private final RedisCache redisCache;
    private final InterfaceInfoMapper interfaceInfoMapper;
    // 每隔 5 秒执行一次将调用次数更新到数据库中（异步）
    @Async("taskExecutor")  // 指定自定义线程池 Bean 名称
    @Scheduled(fixedRate = 5000)
    public void syncInvokeCount() {
        Map<String, Object> cacheMap = redisCache.getCacheMap(SystemConstants.INVOKE_COUNT_PREFIX);
        // 使用 Stream API 将键和值转换为 Long 类型
        Map<Long, Long> longMap = cacheMap.entrySet().stream()
                .filter(entry -> canConvertToLong(entry.getKey()) && canConvertToLong(entry.getValue().toString()))
                .collect(Collectors.toMap(
                        entry -> Long.valueOf(entry.getKey()),
                        entry -> Long.valueOf(entry.getValue().toString())
                ));
        interfaceInfoMapper.updateInvokeCountBatch(longMap);
    }
    private static boolean canConvertToLong(String str) {
        try {
            Long.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // 每天凌晨 1 点执行（异步）
    @Async
    @Scheduled(cron = "0 0 1 * * ?")
    public void task2() {
        System.out.println("Task 2 执行线程: " + Thread.currentThread().getName());
    }
}