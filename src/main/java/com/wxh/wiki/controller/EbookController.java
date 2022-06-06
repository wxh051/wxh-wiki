package com.wxh.wiki.controller;

import com.wxh.wiki.req.EbookQueryReq;
import com.wxh.wiki.req.EbookSaveReq;
import com.wxh.wiki.resp.CommonResp;
import com.wxh.wiki.resp.EbookQueryResp;
import com.wxh.wiki.resp.PageResp;
import com.wxh.wiki.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wxh
 * @date 2022-06-02 12:54
 * <p>
 * //@Controller->返回页面（前后端分离，基本用不到这个注解）
 * //RestController(返回字符串)=controller+responseBody（返回字符串或JSON对象）
 */
@RestController
@RequestMapping("/ebook")
//忽视泛型警告信息
@SuppressWarnings({"rawtypes"})
public class EbookController {

    @Autowired
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(EbookQueryReq req){
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list= ebookService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    //@RequestBody这个注解对应的就是JSON方式的post提交，Content-Type: application/json，前端放在body里，后端通过RequestBody才能接收到
    //如果通过form方式提交（x-www-form-urlencoded）则不用加这个注解
    public CommonResp save(@RequestBody EbookSaveReq req){
        CommonResp resp = new CommonResp<>();
        ebookService.save(req);
        return resp;
    }
}
