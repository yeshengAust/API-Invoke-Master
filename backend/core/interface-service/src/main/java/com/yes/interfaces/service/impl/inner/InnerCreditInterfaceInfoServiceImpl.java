package com.yes.interfaces.service.impl.inner;

import com.yes.common.domain.entity.Log;
import com.yes.common.domain.entity.LogDetails;
import com.yes.common.domain.entity.UserCreditInfo;
import com.yes.common.service.inner.InnerCreditInterfaceInfoService;
import com.yes.interfaces.mapper.InterfaceInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerCreditInterfaceInfoServiceImpl implements InnerCreditInterfaceInfoService {
    @Resource
    InterfaceInfoMapper mapper;

    @Override
    public UserCreditInfo getCreditInfo(String accessKey, String secretKey) {
        UserCreditInfo creditInfo = new UserCreditInfo();
        creditInfo.setAccessKey(accessKey);
        creditInfo.setSecretKey(secretKey);
        UserCreditInfo userCreditInfo = mapper.getUserCreditInfo(creditInfo);
        return userCreditInfo;
    }

    @Override
    public void updateCreditRemain(Long creditId, Long cost) {

        mapper.updateCreditRemain(creditId, cost);
    }

    @Override
    public Long saveInterfaceInvokeLog(Log log) {
       return mapper.saveInterfaceInvokeLog(log);
    }

    @Override
    public void saveInterfaceInvokeDetailsLog(LogDetails logDetails) {
        mapper.saveInterfaceInvokeDetailsLog(logDetails);
    }

    @Override
    public void updateUserInterfaceInvokeCount(Long userId, Long id) {
        Long userInterfaceCount = mapper.getUserInterfaceCount(userId, id);
        if (userInterfaceCount < 1) {
            mapper.saveUserInterfaceInvokeCount(userId, id);
        } else {
            mapper.updateUserInterfaceInvokeCount(userId, id);
        }
    }
}
