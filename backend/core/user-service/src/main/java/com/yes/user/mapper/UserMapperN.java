package com.yes.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yes.common.domain.entity.User;
import com.yes.user.domain.dto.ChargeDto;
import com.yes.user.domain.dto.CreditDto;
import com.yes.user.domain.entity.Credit;

import com.yes.user.domain.vo.CreditVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapperN extends BaseMapper<User> {

     void saveCredit(Credit credit);
     List<CreditVo> pageListCredit(CreditDto dto);
     Long getTotalUserCount (Long userId);


     void forbid(Long id,Integer status);

    void charge(ChargeDto dto);
    void charRemainQuota(Long creditId,Long quota);
    void saveUserRoleInfo(Long userId,Long roleId);
    void updateUserAllQuota(Long userId,Long cost);
    void updateUserAllWallet(Long userId,Long cost);
    void updateUserAvatar(String avatar,Long userId);
}
