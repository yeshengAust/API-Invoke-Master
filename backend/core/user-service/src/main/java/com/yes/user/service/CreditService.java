package com.yes.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yes.common.domain.dto.PageDto;
import com.yes.common.utils.ResponseResult;
import com.yes.user.domain.dto.CreditDto;
import com.yes.user.domain.dto.QuotaChargeDto;
import com.yes.user.domain.entity.Credit;

/**
* @author 叶苗
* @description 针对表【credit】的数据库操作Service
* @createDate 2025-03-07 20:39:35
*/
public interface CreditService extends IService<Credit> {

    ResponseResult saveCredit(CreditDto dto);



    ResponseResult pageCredit(CreditDto dto);

    ResponseResult delete(Long id);



    ResponseResult updateStatus(Long id, Integer status);

    ResponseResult handleRecharge(QuotaChargeDto dto);
}
