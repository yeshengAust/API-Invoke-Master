package com.yes.service.service.serviceImpl;


import com.yes.common.enums.ErrorCode;
import com.yes.common.exception.SystemException;
import com.yes.service.service.ImageService;
import com.yes.service.utils.BaiduUtil;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service

public class ImageServiceImpl implements ImageService {
    @Override
    public String cartoonImage(String base) {
        String res = base;
        try {
            res = BaiduUtil.cartoonImage(base);
        } catch (Exception e) {
            throw new SystemException(ErrorCode.SYSTEM_ERROR);
        }
        return res;
    }
}
