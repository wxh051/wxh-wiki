package com.wxh.wiki.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author wxh
 * @date 2022-06-02 12:54
 * <p>
 * //@Controller->返回页面（前后端分离，基本用不到这个注解）
 * //RestController(返回字符串)=controller+responseBody（返回字符串或JSON对象）
 */
@RestController
public class TestController {

    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

    @Value("${test.hello}")
    private String testhello;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * GET PUT POST DELETE
     * RequestMapping表示这个接口支持所有的请求方式
     */
    @GetMapping("/hello")
    public String hello() {
        return "hello world!" + testhello;
    }

    @PostMapping("/hello/post")
    public String helloPost(String name) {
        return "hello world! Post," + name;
    }

    @RequestMapping("/redis/set/{key}/{value}")
    public String set(@PathVariable Long key, @PathVariable String value) {
        redisTemplate.opsForValue().set(key, value, 3600, TimeUnit.SECONDS);
        LOG.info("key: {}, value: {}", key, value);
        return "success";
    }

    @RequestMapping("/redis/get/{key}")
    public Object get(@PathVariable Long key) {
        Object object = redisTemplate.opsForValue().get(key);
        LOG.info("key: {}, value: {}", key, object);
        return object;
    }
}
