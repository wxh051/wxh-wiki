package com.wxh.wiki.controller;

import com.wxh.wiki.req.CategoryQueryReq;
import com.wxh.wiki.req.CategorySaveReq;
import com.wxh.wiki.resp.CommonResp;
import com.wxh.wiki.resp.CategoryQueryResp;
import com.wxh.wiki.resp.PageResp;
import com.wxh.wiki.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author wxh
 * @date 2022-06-02 12:54
 */
@RestController
@RequestMapping("/category")
//忽视泛型警告信息
@SuppressWarnings({"rawtypes"})
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public CommonResp list(@Valid CategoryQueryReq req) {
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp<>();
        PageResp<CategoryQueryResp> list = categoryService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody CategorySaveReq req) {
        CommonResp resp = new CommonResp<>();
        categoryService.save(req);
        return resp;
    }

    //使用restful风格
    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        categoryService.delete(id);
        return resp;
    }
}
