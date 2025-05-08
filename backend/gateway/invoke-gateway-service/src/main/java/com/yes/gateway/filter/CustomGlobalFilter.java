package com.yes.gateway.filter;


import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.C;
import com.yes.common.constants.SystemConstants;
import com.yes.common.domain.entity.InterfaceInfo;
import com.yes.common.domain.entity.LogDetails;
import com.yes.common.domain.entity.LogTransport;
import com.yes.common.domain.entity.UserCreditInfo;
import com.yes.common.enums.ErrorCode;
import com.yes.common.exception.SystemException;
import com.yes.common.service.inner.InnerCreditInterfaceInfoService;
import com.yes.common.service.inner.InnerInterfaceInfoService;
import com.yes.common.service.inner.InnerUserService;
import com.yes.common.utils.RedisCache;
import com.yes.common.utils.SignUtil;
import com.yes.gateway.config.rabbitmq.RabbitMqExAndQu;
import com.yes.gateway.constant.Constant;
import com.yes.gateway.utils.TokenBucketLimiter;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 全局过滤
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference(check = false)
    private InnerUserService innerUserService;
    @DubboReference(check = false)
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerCreditInterfaceInfoService innerCreditInterfaceInfoService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    RedisCache redisCache;
    @Resource
    RedisTemplate<String,Object> redisTemplate;
    @Autowired
    TokenBucketLimiter tokenBucketLimiter;

    @Override

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 请求日志
        Long trcStart = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();

        ServerHttpResponse response = exchange.getResponse();

        // 3. 用户鉴权（判断 ak、sk 是否合法）
        HttpHeaders headers = request.getHeaders();

        //判断是否超时
        //检查时间戳和随机数防止请求重放
        String timestamp = headers.getFirst("timestamp");
        String nonce = headers.getFirst("nonce");
        long currentTimeMillis = System.currentTimeMillis();
        if (StrUtil.isBlank(timestamp)) {
            return handleNoAuth(exchange.getResponse(),ErrorCode.NO_AUTH_ERROR);
        }
        if (Math.abs(currentTimeMillis - Long.parseLong(timestamp)) > Constant.TIME_GAP) {
            return handleNoAuth(exchange.getResponse(),ErrorCode.NO_AUTH_ERROR);
        }
        //检查随机数是否重复
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        if (nonce == null) {
            return handleNoAuth(exchange.getResponse(),ErrorCode.NO_AUTH_ERROR);
        }
        if (Boolean.FALSE.equals(redisTemplate.hasKey(Constant.NONCE_KEY))) {
            //初始化随机数集合
            //保存随机数和时间戳再通过定时任务删除过期随机数

            addNonceToSet(zSetOperations, nonce);
        } else {
            //判断随机数是否存在
            Double score = zSetOperations.score(Constant.NONCE_KEY, nonce);
            //随机数存在则让请求失效
            if (score != null) {
                return handleNoAuth(exchange.getResponse(),ErrorCode.NO_AUTH_ERROR);
            }
            //保存随机数
            addNonceToSet(zSetOperations, nonce);
        }
        //获取令牌信息
        String accessKey = headers.getFirst(Constant.ACCESS_KEY);
        // 查询令牌数据
        UserCreditInfo creditInfo = null;
        //
        try {
            creditInfo = innerCreditInterfaceInfoService.getCreditInfo(accessKey);
        } catch (Exception e) {
            log.error("getCredit error", e);
        }
        if (creditInfo == null) {
            return handleNoAuth(response,ErrorCode.CREDIT_ERROR);
        }

        //校验密钥
        String headerSign = headers.getFirst(Constant.SIGN);
        String sign = SignUtil.generateSign(Constant.BODY_KEY, creditInfo.getSecretKey());
        if (!sign.equals(headerSign)) {
            return handleNoAuth(exchange.getResponse(),ErrorCode.NO_AUTH_ERROR);
        }
        //利用令牌桶算法对请求进行限流
        if (tokenBucketLimiter.isLimited()) {
            return handleNoAuth(exchange.getResponse(),ErrorCode.NO_AUTH_ERROR);
        }



        //  请求的模拟接口是否存在，以及请求方法是否匹配(通过请求路径查询)
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(request.getPath().toString());
        } catch (Exception e) {
            log.error("getInterfaceInfo error", e);
        }
        if (interfaceInfo == null) {
            return handleNoAuth(response,ErrorCode.INTERFACE_NOT_EXISTS);
        }
        //判断令牌是否匹配接口类型
        if (!interfaceInfo.getSortId().equals(creditInfo.getSortId())) {
            return handleNoAuth(response,ErrorCode.CREDIT_ERROR);
        }
        //判断令牌的余额是否充足
        if (creditInfo.getRemain() <= 0 || creditInfo.getRemain() < interfaceInfo.getCost()) {
            return handleNoAuth(response,ErrorCode.CREDIT_BALANCE_NOT_ENOUGH);
        }
        Long invokeStart = System.currentTimeMillis();


        //符合条件后进行调用
        String params = request.getQueryParams().toString();
        return handleResponse(exchange, chain, interfaceInfo,creditInfo,params,trcStart,invokeStart);


    }
    /**
     * 保存随机数
     *
     * @param zSetOperations zSet集合
     * @param nonce          随机数
     */
    private void addNonceToSet(ZSetOperations<String, Object> zSetOperations, String nonce) {
        try {
            zSetOperations.add(Constant.NONCE_KEY, nonce, System.currentTimeMillis() / 1000.0);
        } catch (Exception e) {
            log.error("redis set key error", e);
            throw new SystemException(ErrorCode.PARAMS_ERROR);
        }
    }

    /**
     * 创建日志
     *
     * @param userCreditInfo
     * @param interfaceInfo
     * @param useTime
     * @param invokeTime
     * @return
     */
    private LogTransport getLog(UserCreditInfo userCreditInfo, InterfaceInfo interfaceInfo, Long useTime, Long invokeTime,String requestParams,byte[] allContent) {
        LogTransport log = new LogTransport();
        log.setCost(interfaceInfo.getCost());
        log.setCreateTime(new Date());
        log.setCreditId(userCreditInfo.getCreditId());
        log.setGroupId(SystemConstants.GROUP_BUY);
        log.setUserId(userCreditInfo.getUserId());
        log.setUseTime(useTime);
        log.setInvokeTime(invokeTime);
        log.setInterfaceId(interfaceInfo.getId());
        if (requestParams.length()<=100)
            log.setRequestParams(requestParams);

        String responseBody = new String(allContent, StandardCharsets.UTF_8);
        if (responseBody.length() <=10000)
            log.setResponse(responseBody);
        return log;
    }

    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, InterfaceInfo interfaceInfo, UserCreditInfo userCreditInfo,String requestParams ,Long trcStart, Long invokeStart) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return fluxBody.collectList().flatMap(dataBuffers -> {

                                Long trcEnd = System.currentTimeMillis();
                                Long invokeEnd = System.currentTimeMillis();
                                Long useTime = trcEnd - trcStart;
                                Long invokeTime = invokeEnd - invokeStart;

                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                for (DataBuffer dataBuffer : dataBuffers) {
                                    byte[] content = new byte[dataBuffer.readableByteCount()];
                                    dataBuffer.read(content);
                                    DataBufferUtils.release(dataBuffer);
                                    bos.write(content, 0, content.length);
                                }
                                byte[] allContent = bos.toByteArray();

                                try {
//                                    //扣减响应令牌的剩余额度
//                                    innerCreditInterfaceInfoService.updateCreditRemain(userCreditInfo.getCreditId(), interfaceInfo.getCost());
//                                    //添加用户接口调用次数
//                                    innerCreditInterfaceInfoService.updateUserInterfaceInvokeCount(userCreditInfo.getUserId(), interfaceInfo.getId());
//                                    //添加用户的总调用额度
//                                    innerUserService.updateUserAllQuota(userCreditInfo.getUserId(),interfaceInfo.getCost());
                                    //构建日志

                                    LogTransport log = getLog(userCreditInfo, interfaceInfo, useTime, invokeTime,requestParams,allContent);
                                    //开启异步
                                    innerUserService.invokeInterfaceOk(userCreditInfo,interfaceInfo,log);

                                } catch (Exception e) {
                                    log.error("invokeCount error", e);
                                }

//                                // 构建日志
//                                StringBuilder sb2 = new StringBuilder(200);
//                                List<Object> rspArgs = new ArrayList<>();
//                                rspArgs.add(originalResponse.getStatusCode());
//                                String data = new String(allContent, StandardCharsets.UTF_8);
//                                sb2.append(data);
//                                // 打印日志

                                return super.writeWith(Flux.just(bufferFactory.wrap(allContent)));
                            });

//                            return super.writeWith(
//                                    fluxBody.map(dataBuffer -> {
//                                        Long trcEnd = System.currentTimeMillis();
//                                        Long invokeEnd = System.currentTimeMillis();
//                                        Long useTime = trcEnd - trcStart;
//                                        Long invokeTime = invokeEnd - invokeStart;
//
//                                        byte[] content=null;
//                                        // 7. 调用成功，调用rabbitmq远程服务接口调用次数 + 1 并且写入日志
//                                        try {
//
//                                            //扣减响应令牌的剩余额度
//                                            innerCreditInterfaceInfoService.updateCreditRemain(userCreditInfo.getCreditId(), interfaceInfo.getCost());
//                                            //添加用户接口调用次数
//                                            innerCreditInterfaceInfoService.updateUserInterfaceInvokeCount(userCreditInfo.getUserId(),interfaceInfo.getId());
//                                            //构建日志
//                                            LogTransport log = getLog(userCreditInfo, interfaceInfo, useTime, invokeTime);
//                                            log.setRequestParams(requestParams);
//                                            content = new byte[dataBuffer.readableByteCount()];
//                                            dataBuffer.read(content);
//                                            DataBufferUtils.release(dataBuffer);
//
//                                            String responseBody = new String(content, StandardCharsets.UTF_8);
//                                            log.setResponse(responseBody);
//
//                                            rabbitTemplate.convertAndSend(RabbitMqExAndQu.LOG_EXCHANGE, RabbitMqExAndQu.LOG_RK,log );
//
//                                        } catch (Exception e) {
//                                            log.error("invokeCount error", e);
//                                        }
//
//                                        // 构建日志
//                                        StringBuilder sb2 = new StringBuilder(200);
//                                        List<Object> rspArgs = new ArrayList<>();
//                                        rspArgs.add(originalResponse.getStatusCode());
//                                        String data = new String(content, StandardCharsets.UTF_8); //data
//                                        sb2.append(data);
//                                        // 打印日志
//                                        log.info("响应结果：" + data);
//                                        return bufferFactory.wrap(content);
//                                    }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response, ErrorCode errorCode) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.setRawStatusCode(errorCode.getCode());
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}