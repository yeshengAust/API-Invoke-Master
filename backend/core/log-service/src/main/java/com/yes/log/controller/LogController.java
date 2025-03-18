package com.yes.log.controller;

import com.yes.common.utils.ResponseResult;
import com.yes.log.domain.dto.LogDto;
import com.yes.log.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
public class LogController {
    @Autowired
    LogService logService;

    @GetMapping("/page")
    ResponseResult pageLogs(LogDto dto) {
        return logService.pageLogs(dto);
    }

    @GetMapping("/count")
    ResponseResult countInfo() {
        return logService.countInfo();
    }

    @GetMapping("/allInterfaceCount")
    public ResponseResult allInterfaceCount() {
        return logService.allInterfaceCount();
    }

    @GetMapping("/allInvokeCount")
    public ResponseResult allInvokeCount() {
        return logService.allInvokeCount();
    }

    @GetMapping("/growthNumsToday")
    public ResponseResult growthNumsToday() {
        return logService.growthNumsToday();
    }

    @GetMapping("/growthNumsRateToday")
    public ResponseResult growthNumsRateToday() {
        return logService.growthNumsRateToday();
    }
//    @GetMapping("/sevenDays")
//    public ResponseResult getSevenDays() {
//        return logService.getSevenDays();
//    }
    @GetMapping("/sevenData")
    public ResponseResult getSevenData() {
        return logService.getSevenData();
    }
    @GetMapping("/hotInterfaces")
    public ResponseResult getHotInterfaces() {
        return logService.getHotInterfaces();
    }
    @GetMapping("/allSortValue")
    public ResponseResult getAllSortValue() {
        return logService.getAllSortValue();
    }

}
