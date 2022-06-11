package com.wxh.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wxh.wiki.domain.Category;
import com.wxh.wiki.domain.CategoryExample;
import com.wxh.wiki.mapper.CategoryMapper;
import com.wxh.wiki.req.CategoryQueryReq;
import com.wxh.wiki.req.CategorySaveReq;
import com.wxh.wiki.resp.CategoryQueryResp;
import com.wxh.wiki.resp.PageResp;
import com.wxh.wiki.util.CopyUtil;
import com.wxh.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author wxh
 * @date 2022-06-02 17:00
 */
@Service
public class CategoryService {
    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);


    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SnowFlake snowFlake;

    public List<CategoryQueryResp> all() {
        CategoryExample categoryExample = new CategoryExample();
        //排序
        categoryExample.setOrderByClause("sort asc");
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

        //列表复制
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        return list;
    }

    public PageResp<CategoryQueryResp> list(CategoryQueryReq req) {
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");

        CategoryExample.Criteria criteria = categoryExample.createCriteria();

        //从1开始。只对第一个遇到的select起作用
        PageHelper.startPage(req.getPage(),req.getSize());
        //持久层返回List<Category>需要转换成List<CategoryResp>再返回Controller
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

        PageInfo<Category>pageInfo=new PageInfo<>(categoryList);
        //一般前端分页组件只需要total，就会自己计算出pages
        LOG.info("总行数：{}",pageInfo.getTotal());
        LOG.info("总页数：{}",pageInfo.getPages());

        //列表复制
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        PageResp<CategoryQueryResp>pageResp=new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    /**
     * 保存
     * @param req
     */
    public void save(CategorySaveReq req) {
        Category category=CopyUtil.copy(req,Category.class);
        if(ObjectUtils.isEmpty(req.getId())){
            //新增
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        }else {
            //更新
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    //暂未解决，删除子分类的问题
    public void delete(Long id){
        categoryMapper.deleteByPrimaryKey(id);
    }
}
