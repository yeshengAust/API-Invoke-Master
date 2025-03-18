package com.yes.user.service;

import com.yes.common.utils.ResponseResult;
import com.yes.user.domain.dto.ChargeDto;

public interface WalletService {
    ResponseResult getWallet(Long userId);

    ResponseResult charge(ChargeDto dto);

    ResponseResult getQuota();
}
