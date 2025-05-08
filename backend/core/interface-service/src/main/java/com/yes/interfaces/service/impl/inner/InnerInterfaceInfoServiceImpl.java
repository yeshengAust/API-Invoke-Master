package com.yes.interfaces.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yes.common.domain.entity.InterfaceInfo;
import com.yes.common.service.inner.InnerInterfaceInfoService;
import com.yes.common.utils.ResponseResult;
import com.yes.interfaces.mapper.InterfaceInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;

@DubboService(group = "api", interfaceClass = InnerInterfaceInfoService.class)
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
    @Resource
    InterfaceInfoMapper mapper;

    @Override
    public InterfaceInfo getInterfaceInfo(String url) {
        LambdaQueryWrapper<InterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InterfaceInfo::getUrl,url);
        InterfaceInfo interfaceInfo = mapper.selectOne(queryWrapper);
        return interfaceInfo;
    }
    /**
     * 所有接口数量
     *
     * @return
     */
    @Override
    public Long allInterfaceCount() {
        return mapper.allInterfaceCount();
    }

    /**
     *查询前三条热门的接口
     * @return
     */
    @Override
    public List<Long> hotInterfaces() {
        List<Long> hotInterfaces = mapper.getHotInterfaces();
        return hotInterfaces;
    }

    @Override
    public List<InterfaceInfo> getInterfaceInfoByIds(List<Long> interfaceIds) {
        return  mapper.selectBatchIds(interfaceIds);
    }


    /**
     * 总的调用次数
     */
    @Override
    public Long allInvokeCount(){
        return mapper.allInvokeCount();
    }


}
