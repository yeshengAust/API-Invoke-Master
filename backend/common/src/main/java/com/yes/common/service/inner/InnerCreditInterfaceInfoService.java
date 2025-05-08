package com.yes.common.service.inner;

import com.yes.common.domain.entity.Log;
import com.yes.common.domain.entity.LogDetails;
import com.yes.common.domain.entity.UserCreditInfo;

public interface InnerCreditInterfaceInfoService {
    UserCreditInfo getCreditInfo(String accessKey);
    void updateCreditRemain(Long creditId,Long cost);
    Long saveInterfaceInvokeLog(Log log);
    void saveInterfaceInvokeDetailsLog(LogDetails logDetails);

    void updateUserInterfaceInvokeCount(Long userId, Long id);
}
