package com.yes.log.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yes.common.domain.entity.Log;
import com.yes.common.utils.ResponseResult;
import com.yes.log.domain.dto.LogDto;

import java.util.ArrayList;


/**
* @author 叶苗
* @description 针对表【log】的数据库操作Service
* @createDate 2025-03-09 13:16:33
*/
public interface LogService extends IService<Log> {

    ResponseResult pageLogs(LogDto dto);

    ResponseResult countInfo();

    ResponseResult allInterfaceCount();

    ResponseResult allInvokeCount();

    ResponseResult growthNumsToday();
   ResponseResult  growthNumsRateToday();
    ArrayList<String> getSevenDays();
   ResponseResult getSevenData();

    ResponseResult getHotInterfaces();

    ResponseResult getAllSortValue();
}
