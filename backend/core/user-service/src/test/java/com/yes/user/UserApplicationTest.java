package com.yes.user;

import com.yes.common.utils.RedisCache;
import com.yes.user.utils.UserUtils;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.yes.user.utils.OssUploadUtil.getCDNDomainsByBucket;

@SpringBootTest
class UserApplicationTest {

    @Autowired
    RedisCache redisCache;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void test() {
        String encode = UserUtils.encode("xiaolvdada6");
        System.out.println(encode);
    }


    @Test
    public void tt(){
        String accessKey = "YZxoCPdyMfaSI1Cnbw0dKRibKyvKeb5pbqn4swEv";
        String secretKey = "SjoXqKqCpsaEJkBYEbFYBSKXvCblsRUbGvuNGYGm";
        String bucketName = "yes-blog";
        String domains = getCDNDomainsByBucket(accessKey, secretKey, bucketName);
        System.out.println(domains);
    }
}