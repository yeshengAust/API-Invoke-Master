package com.yes.log.service.impl.inner;

import com.yes.common.domain.entity.Log;
import com.yes.common.domain.entity.LogDetails;
import com.yes.common.service.inner.InnerLogService;
import com.yes.log.mapper.LogMapper;
import com.yes.log.service.impl.LogServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(group = "api", interfaceClass = InnerLogService.class)
@RequiredArgsConstructor
public class InnerLogUserImpl implements InnerLogService {
    private final LogMapper mapper;
    @Override
    public void saveLog(Log log) {
        mapper.insert(log);
    }

    @Override
    public void saveLogDetails(LogDetails log) {

    }
}
