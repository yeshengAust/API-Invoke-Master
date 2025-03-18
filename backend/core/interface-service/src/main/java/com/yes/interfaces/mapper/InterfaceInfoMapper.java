package com.yes.interfaces.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yes.common.domain.entity.Log;
import com.yes.common.domain.entity.LogDetails;
import com.yes.common.domain.entity.InterfaceInfo;
import com.yes.common.domain.entity.UserCreditInfo;
import com.yes.interfaces.domain.vo.InterfaceExampleVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author 叶苗
* @description 针对表【interface_info(接口信息)】的数据库操作Mapper
* @createDate 2025-02-27 11:17:57
* @Entity generator.domain.InterfaceInfo
*/
@Mapper
public interface InterfaceInfoMapper extends BaseMapper<InterfaceInfo> {
   Long getInterfaceCost(Long interfaceId);
   void updateCreditRemain(Long creditId,Long cost);
   Long saveInterfaceInvokeLog(Log log);
   UserCreditInfo getUserCreditInfo(UserCreditInfo userCreditInfo);

   void saveInterfaceInvokeDetailsLog(LogDetails logDetails);
   void updateUserInterfaceInvokeCount(Long userId, Long interfaceId);
   Long getUserInterfaceCount(Long userId, Long interfaceId);
   void saveUserInterfaceInvokeCount(Long userId, Long interfaceId);

    Long allInvokeCount();
   Long allInterfaceCount();



    List<InterfaceInfo> getHotInterfaces();
    InterfaceExampleVo getInterfaceExample(Long interfaceId);
}




