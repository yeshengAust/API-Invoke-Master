package com.yes.buy.service;

import com.yes.buy.domain.dto.BuyProductDto;
import com.yes.common.utils.ResponseResult;

public interface BuyService {
   ResponseResult buy(BuyProductDto dto) throws Exception;
}
