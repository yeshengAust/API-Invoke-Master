package com.yes.common.service.inner;

import com.yes.common.domain.entity.Log;
import com.yes.common.domain.entity.LogDetails;

public interface InnerLogService {
    void saveLog(Log log);
    void saveLogDetails(LogDetails log);
}
