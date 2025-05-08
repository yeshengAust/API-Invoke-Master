package com.yes.asyncmessage.listener;

import com.alibaba.fastjson.JSON;
import com.yes.asyncmessage.config.rabbitmq.RabbitMqExAndQu;
import com.yes.asyncmessage.utils.EmailSenderUtil;

import com.yes.common.config.security.UserLogin;
import com.yes.common.constants.SystemConstants;
import com.yes.common.domain.dto.CacheDto;
import com.yes.common.domain.entity.Log;
import com.yes.common.enums.ErrorCode;
import com.yes.common.exception.SystemException;
import com.yes.common.service.inner.InnerLogService;
import com.yes.common.utils.BeanCopyUtils;
import com.yes.common.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.yes.asyncmessage.config.rabbitmq.RabbitMqExAndQu.LOG_EXCHANGE;


@Component
@Slf4j
public class RedisListener {
    @Autowired
    RedisCache redisCache;
    @Autowired
    EmailSenderUtil emailSenderUtil;

    @DubboReference(check = false)
    private InnerLogService innerLogService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "user_info_queue", durable = "true"), // 队列 起名规则（服务名+业务名+成功+队列），durable持久化
            exchange = @Exchange(name = "user_info_exg"), // 交换机名称，交换机默认类型就行direct，所以不用配置direct
            key = {"user_info"}// 绑定的key
    ))
    public void userCache(CacheDto cacheDto) {
      log.info("正在写入用户信息缓存");
        //把数据写入redis
        redisCache.setCacheObject(cacheDto.getKey(), (UserLogin)cacheDto.getValue(), SystemConstants.USER_INFO_EXPIRE_TIME,SystemConstants.EXPIRE_UNIT);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "invoke_ok_queue", durable = "true"), // 队列 起名规则（服务名+业务名+成功+队列），durable持久化
            exchange = @Exchange(name = "invoke_ok_exg"), // 交换机名称，交换机默认类型就行direct，所以不用配置direct
            key = {"invoke_ok"}// 绑定的key
    ))
    public void invokeOK(CacheDto cacheDto) {
        log.info("正在写入用户信息缓存");
        //把数据写入redis
        redisCache.setCacheMap(cacheDto.getKey(),(Map<String, Integer>) cacheDto.getValue());
    }



    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "verify_code_queue", durable = "true"), // 队列 起名规则（服务名+业务名+成功+队列），durable持久化
            exchange = @Exchange(name = RabbitMqExAndQu.REDIS_EXCHANGE), // 交换机名称，交换机默认类型就行direct，所以不用配置direct
            key = {"verify_code"}// 绑定的key
    ))
    public void verifyCode(CacheDto cacheDto) {
        log.info("正在写入redis-----------");
        //把数据写入redis
        redisCache.setCacheObject(cacheDto.getKey(),cacheDto.getValue(), SystemConstants.VERIFY_EXPIRE_TIME,SystemConstants.EXPIRE_UNIT);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "email.queue", durable = "true"), // 队列 起名规则（服务名+业务名+成功+队列），durable持久化
            exchange = @Exchange(name = RabbitMqExAndQu.EMAIL_EXCHANGE), // 交换机名称，交换机默认类型就行direct，所以不用配置direct
            key = {RabbitMqExAndQu.EMAIL_RK}// 绑定的key
    ))
    public void sendEmail(CacheDto cacheDto) {
        log.info("正在写发送邮箱验证-----------");
        //发送邮箱
        String[] split = cacheDto.getKey().split(":");
        String email = split[split.length - 1];
        String code = (String) cacheDto.getValue();
        String context = "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>验证码界面</title>\n" +
                "</head>\n" +
                "<body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;\">\n" +
                "  <div style=\"background-color: white; width: 100%; min-height: 100vh; display: flex; justify-content: center; align-items: center;\">\n" +
                "    <p style=\"font-size: 48px; font-weight: bold; text-align: center; color: black; letter-spacing: 10px;\">"+code+"</p>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>";
        try {
            emailSenderUtil.sendSimpleEmail(email,"验证码", context);
        }
        catch (Exception e){
            throw new SystemException(ErrorCode.SYSTEM_ERROR);
        }
        //把数据写入redis
        redisCache.setCacheObject(cacheDto.getKey(),cacheDto.getValue(), SystemConstants.REGISTRY_EXPIRE_TIME,SystemConstants.EXPIRE_UNIT);
    }
    //日志
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "log_queue", durable = "true"), // 队列 起名规则（服务名+业务名+成功+队列），durable持久化
            exchange = @Exchange(name = LOG_EXCHANGE), // 交换机名称，交换机默认类型就行direct，所以不用配置direct
            key = {RabbitMqExAndQu.LOG_RK}// 绑定的key
    ))
    public void sendLog(CacheDto cacheDto) {
        log.info("正在写日志-----------");
        //把数据写入redis
        innerLogService.saveLog(BeanCopyUtils.copyBean(cacheDto.getValue(), Log.class));
    }


}
