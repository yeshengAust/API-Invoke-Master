package com.yes.log;

import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import com.yes.common.utils.ResponseResult;
import com.yes.log.mapper.LogMapper;
import com.yes.log.service.LogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class RR {

    @Autowired
    private LogService logMapper;

    @Test
    void contextLoads() {
        List<Long> arr = new ArrayList<>();
        arr.add(1L);
        arr.add(2L);
        ResponseResult hotInterfaces = logMapper.getHotInterfaces();
        System.out.println(hotInterfaces);
    }
}
