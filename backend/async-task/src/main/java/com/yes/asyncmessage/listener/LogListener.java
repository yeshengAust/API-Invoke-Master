package com.yes.asyncmessage.listener;


import com.yes.asyncmessage.config.rabbitmq.RabbitMqExAndQu;
import com.yes.common.domain.entity.Log;
import com.yes.common.domain.entity.LogDetails;
import com.yes.common.domain.entity.LogTransport;
import com.yes.common.service.inner.InnerCreditInterfaceInfoService;
import com.yes.common.utils.BeanCopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogListener {

    @DubboReference(check = false)
    InnerCreditInterfaceInfoService service;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "log_queue", durable = "true"), // 队列 起名规则（服务名+业务名+成功+队列），durable持久化
            exchange = @Exchange(name = RabbitMqExAndQu.LOG_EXCHANGE), // 交换机名称，交换机默认类型就行direct，所以不用配置direct
            key = {RabbitMqExAndQu.LOG_RK}// 绑定的key
    ))
    public void log(LogTransport transport) {
        log.info("正在写入日志-----------");
        //将日志写入到mysql中
        Log log = BeanCopyUtils.copyBean(transport, Log.class);
        Long logId = service.saveInterfaceInvokeLog(log);
        //将详细日志写入mysql中
        LogDetails logDetails = BeanCopyUtils.copyBean(transport, LogDetails.class);
        logDetails.setLogId(logId);
        service.saveInterfaceInvokeDetailsLog(logDetails);
    }
}
