package com.yes.asyncmessage.service;

import com.yes.common.domain.dto.CacheDto;
import com.yes.common.domain.dto.SendMailDto;
import com.yes.common.utils.ResponseResult;

public interface AsyncMessageService {
    ResponseResult sendMail(SendMailDto sendMailDto);

    ResponseResult codeCache(CacheDto cacheDto);
}
