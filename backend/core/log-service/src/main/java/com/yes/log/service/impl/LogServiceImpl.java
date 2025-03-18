package com.yes.log.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yes.common.config.security.SecurityUtils;
import com.yes.common.domain.entity.InterfaceInfo;
import com.yes.common.domain.entity.Log;
import com.yes.common.domain.vo.PageVo;
import com.yes.common.service.inner.InnerInterfaceInfoService;
import com.yes.common.utils.BeanCopyUtils;
import com.yes.common.utils.ResponseResult;
import com.yes.log.domain.dto.LogDto;

import com.yes.log.domain.entity.AllSortValue;
import com.yes.log.domain.entity.DayData;
import com.yes.log.domain.vo.DayDataVo;
import com.yes.log.domain.vo.HotInterfaceVo;
import com.yes.log.domain.vo.LogCountVo;
import com.yes.log.domain.vo.LogVo;
import com.yes.log.mapper.LogMapper;
import com.yes.log.service.LogService;
import org.apache.catalina.security.SecurityUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
* @author 叶苗
* @description 针对表【log】的数据库操作Service实现
* @createDate 2025-03-09 13:16:33
*/
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log>
    implements LogService {
    @Autowired
    LogMapper logMapper;
    @DubboReference(check = false)
    InnerInterfaceInfoService innerInterfaceInfoService;
    /**
     * 分页查询日志信息
     * @param dto
     * @return
     */
    @Override
    public ResponseResult pageLogs(LogDto dto) {
        dto.setPageNum(dto.getPageNum()-1);
        Long userId = SecurityUtils.getUserId();
        dto.setUserId(userId);
        List<LogVo> logVos = logMapper.pageLogs(dto);
        List<LogVo> all = logMapper.pageLogsAll(dto);
        Long total = Long.valueOf(all.size());

        PageVo pageVo = new PageVo(logVos,total);
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 统计令牌的数据信息
     * @return
     */
    @Override
    public ResponseResult countInfo() {
        LogDto logDto = new LogDto();
        logDto.setGroupId(2);
        List<LogVo> logVos = logMapper.pageLogsAll(logDto);
        //统计全部的使用金额
        Long quota = logVos.stream().mapToLong(LogVo::getCost).sum();
        //统计流量频率
        Long useTime = logVos.stream().mapToLong(LogVo::getUseTime).sum();
        Long invokeTime = logVos.stream().mapToLong(LogVo::getInvokeTime).sum();
        Long tpm =(long)(logVos.size()/convertMillisToSeconds(useTime));
        Long rpm =(long)(logVos.size()/convertMillisToSeconds(invokeTime));
        return ResponseResult.okResult(new LogCountVo(quota,rpm,tpm));
    }

    /**
     * 获取所有接口的数量
     * @return
     */
    @Override
    public ResponseResult allInterfaceCount() {
       Long count = innerInterfaceInfoService.allInterfaceCount();
        return ResponseResult.okResult(count);
    }

    /***
     * 获取所有接口调用的次数
     * @return
     */
    @Override
    public ResponseResult allInvokeCount() {
        Long count = innerInterfaceInfoService.allInvokeCount();
        return ResponseResult.okResult(count);
    }

    /**
     * 获取当天的调用次数
     * @return
     */
    @Override
    public ResponseResult  growthNumsToday() {
        Long count = logMapper.growthNumsToday();
        return ResponseResult.okResult(count);
    }
    @Override
    public ResponseResult  growthNumsRateToday() {
        Long last = logMapper.growthNumsLastDay();
        Long today = logMapper.growthNumsToday();
        if(last == 0){
            last=1L;
        }
        double rate = 1.0*(today-last)/last*100;
        return ResponseResult.okResult(rate);
    }

    /**
     * 获得最近七天的日期
     * @return
     */
    @Override
    public ArrayList<String> getSevenDays() {
        ArrayList<String> days = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        String cur = currentDate.format(formatter);

        for(int i = 6; i > 0;i--){
            // 计算前6天的日期
            String format = currentDate.minusDays(i).format(formatter);
            days.add(format);
        }
        days.add(cur);
        return days;
    }

    /**
     * 获取近七天的访问量
     * @return
     */
    @Override
    public ResponseResult getSevenData() {
        List<DayData> sevenData = logMapper.getSevenData();
        ArrayList<String> sevenDays = getSevenDays();
        Long[] longs = new Long[sevenDays.size()];
        ArrayList<Long> data = new ArrayList<>();
        for (String sevenDay : sevenDays) {
            data.add(0L);
        }


        for (int i = 0; i < sevenDays.size(); i++) {
            for (DayData sevenDatum : sevenData) {
                if (sevenDays.get(i).equals(sevenDatum.getDays().toString())){
                    data.set(i,sevenDatum.getData());
                }

            }
        }
        DayDataVo dayDataVo = new DayDataVo(sevenDays,data);
        System.out.println(data);
        return ResponseResult.okResult(dayDataVo);
    }

    /**
     * 获取热门接口信息
     * @return
     */
    @Override
    public ResponseResult getHotInterfaces() {
        List<InterfaceInfo> interfaceInfos = innerInterfaceInfoService.hotInterfaces();
        ArrayList<HotInterfaceVo> hotInterfaceVos = new ArrayList<>();
        //获取该接口的日增量
        long sortNum = 1L;
        for (InterfaceInfo interfaceInfo : interfaceInfos) {
            Long curInvokeCount = logMapper.growthNumsTodayByInterfaceId(interfaceInfo.getId());
            Long lastInvokeCount = logMapper.growthNumsLastByInterfaceId(interfaceInfo.getId());
            long diff = curInvokeCount-lastInvokeCount;
            if(lastInvokeCount ==0L) lastInvokeCount=1L;
            double rate = (1.0)*diff/lastInvokeCount*100;
            HotInterfaceVo hotInterfaceVo = new HotInterfaceVo();
            HotInterfaceVo hotInterface = BeanCopyUtils.copyBean(interfaceInfo, HotInterfaceVo.class);
            hotInterface.setUpRate(rate);
            hotInterface.setSort(sortNum++);
            hotInterfaceVos.add(hotInterface);
        }
        return ResponseResult.okResult(hotInterfaceVos);
    }

    /**
     * 获取所有分类的数据
     * @return
     */
    @Override
    public ResponseResult getAllSortValue() {
        List<AllSortValue> allSortValue = logMapper.getAllSortValue();
        return ResponseResult.okResult(allSortValue);
    }


    public static double convertMillisToSeconds(long millis) {
        return (double) millis / 1000;
    }
}




