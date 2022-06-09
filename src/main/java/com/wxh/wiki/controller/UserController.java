package com.wxh.wiki.controller;

import com.wxh.wiki.req.UserLoginReq;
import com.wxh.wiki.req.UserQueryReq;
import com.wxh.wiki.req.UserResetPasswordReq;
import com.wxh.wiki.req.UserSaveReq;
import com.wxh.wiki.resp.CommonResp;
import com.wxh.wiki.resp.PageResp;
import com.wxh.wiki.resp.UserLoginResp;
import com.wxh.wiki.resp.UserQueryResp;
import com.wxh.wiki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author wxh
 * @date 2022-06-09 14:44
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public CommonResp list(@Valid UserQueryReq req) {
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody UserSaveReq req) {
        //把密码设为一个32位的16进制的字符串
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }

    @PostMapping("/reset-password")
    public CommonResp resetPassword(@Valid @RequestBody UserResetPasswordReq req) {
        //把密码设为一个32位的16进制的字符串
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.resetPassword(req);
        return resp;
    }

    @PostMapping("/login")
    public CommonResp login(@Valid @RequestBody UserLoginReq req) {
        //把密码设为一个32位的16进制的字符串
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp = new CommonResp<>();
        UserLoginResp userLoginResp=userService.login(req);
        resp.setContent(userLoginResp);
        return resp;
    }
}


