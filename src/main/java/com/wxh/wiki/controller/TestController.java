package com.wxh.wiki.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wxh
 * @date 2022-06-02 12:54
 * <p>
 * //@Controller->返回页面（前后端分离，基本用不到这个注解）
 * //RestController(返回字符串)=controller+responseBody（返回字符串或JSON对象）
 */
@RestController
public class TestController {

    @Value("${test.hello}")
    private String testhello;

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
}
