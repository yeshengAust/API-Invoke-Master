package com.yes.log.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yes.common.domain.entity.Log;
import com.yes.log.domain.dto.LogDto;

import com.yes.log.domain.entity.AllSortValue;
import com.yes.log.domain.vo.LogVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author 叶苗
* @description 针对表【log】的数据库操作Mapper
* @createDate 2025-03-09 13:16:33
* @Entity generator.domain.Log
*/
@Mapper
public interface LogMapper extends BaseMapper<Log> {
    List<LogVo> pageLogs(LogDto dto);
    List<LogVo> pageLogsAll(LogDto dto);

    Long growthNumsToday();
    Long growthNumsTodayByInterfaceId(Long interfaceId);
    Long growthNumsLastByInterfaceId(Long interfaceId);
    Long growthNumsLastDay();
    List<com.yes.log.domain.entity.DayData> getSevenData();
   List<AllSortValue> getAllSortValue();


}




