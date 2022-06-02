package com.wxh.wiki.controller;

import com.wxh.wiki.domain.Demo;
import com.wxh.wiki.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wxh
 * @date 2022-06-02 12:54
 * <p>
 * //@Controller->返回页面（前后端分离，基本用不到这个注解）
 * //RestController(返回字符串)=controller+responseBody（返回字符串或JSON对象）
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @GetMapping("/list1")
    public List<Demo>list(){
        return demoService.list();
    }

}
