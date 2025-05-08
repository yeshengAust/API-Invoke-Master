package com.yes.asyncmessage.service.impl;

import com.yes.asyncmessage.service.AsyncMessageService;
import com.yes.asyncmessage.utils.EmailSenderUtil;
import com.yes.common.domain.dto.CacheDto;
import com.yes.common.domain.dto.SendMailDto;
import com.yes.common.enums.ErrorCode;
import com.yes.common.exception.SystemException;
import com.yes.common.utils.RedisCache;
import com.yes.common.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsyncMessageServiceImpl implements AsyncMessageService {
    @Autowired
    private EmailSenderUtil emailSenderUtil;
    @Autowired
    RedisCache redisCache;

    @Override
    public ResponseResult sendMail(SendMailDto sendMailDto) {
        String toEmail = sendMailDto.getQqEmail();
        String subject = sendMailDto.getSubject();
        String content = sendMailDto.getContent();
        try {
            emailSenderUtil.sendSimpleEmail(toEmail, subject, content);
        } catch (Exception e) {
            throw new SystemException(ErrorCode.SYSTEM_ERROR);
        }
        return ResponseResult.okResult();
    }

    /**
     * 将数据缓存到redis中
     *
     * @param cacheDto
     * @return
     */
    @Override

    public ResponseResult codeCache(CacheDto cacheDto) {
        System.out.println("调通了");
       try {
           redisCache.setCacheObject(cacheDto.getKey(),cacheDto.getValue());
       }catch (Exception e){
           throw new SystemException(ErrorCode.SYSTEM_ERROR);
       }
        return ResponseResult.okResult();
    }
}
