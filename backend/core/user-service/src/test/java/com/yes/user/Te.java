package com.yes.user;

import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.yes.user.mapper.UserMapperN;
import com.yes.user.service.UserService;
import com.yes.user.service.impl.UserServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.GlobalTransactionContext;
import org.jdom2.output.support.SAXOutputProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@SpringBootTest
public class Te {

    @Autowired
    UserMapperN userMapperN;



    @Test
    public void test() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean matches = bCryptPasswordEncoder.matches("123456", "$2a$10$oW77RxzaFqBJ6oPPer1ZxOwAF5MlMuCz9huZmhHR2.Tj6IXDjbfpu");
        System.out.println(matches);
    }
    @Test
    @GlobalTransactional
    public void test1() {
        userMapperN.updateUserAllWallet(1L,520L);
        System.out.println(1/0);
    }
    @Test
    public void test2() {
     test1();
    }

    @Test
    @GlobalTransactional
    public void test3() {
        // 获取全局事务会话
        GlobalTransaction tx = GlobalTransactionContext.getCurrentOrCreate();
        System.out.println("XID: " + tx.getXid());
// 如果没有数据库连接问题，可以正常获取XID
    }
}
