package com.yes.common.service.inner;


import com.yes.common.domain.entity.InterfaceInfo;
import com.yes.common.domain.entity.LogTransport;
import com.yes.common.domain.entity.User;
import com.yes.common.domain.entity.UserCreditInfo;

/**
 * 内部用户服务
 *
**/
public interface InnerUserService {

    /**
     * 数据库中查是否已分配给用户秘钥（accessKey）
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);
    User getUserById(Long userId);
    void updateUser(User user);

    void updateUserAllQuota(Long userId, Long cost);
    void invokeInterfaceOk(UserCreditInfo userCreditInfo, InterfaceInfo interfaceInfo, LogTransport log);
}
