package com.yes.common.service.inner;

import com.yes.common.domain.entity.InterfaceInfo;

import java.util.List;

/**
 * 内部接口调用服务
 */
public interface InnerInterfaceInfoService {
    InterfaceInfo getInterfaceInfo (String url);
    Long allInvokeCount();
    Long allInterfaceCount();
    List<InterfaceInfo> hotInterfaces();


}
