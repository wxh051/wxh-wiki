package com.wxh.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wxh.wiki.domain.Ebook;
import com.wxh.wiki.domain.EbookExample;
import com.wxh.wiki.mapper.EbookMapper;
import com.wxh.wiki.req.EbookQueryReq;
import com.wxh.wiki.req.EbookSaveReq;
import com.wxh.wiki.resp.EbookQueryResp;
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
public class EbookService {
    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);


    @Autowired
    private EbookMapper ebookMapper;

    @Autowired
    private SnowFlake snowFlake;

    public PageResp<EbookQueryResp> list(EbookQueryReq req) {
        EbookExample ebookExample = new EbookExample();
        //相当于where条件
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        //动态SQL
        if(!ObjectUtils.isEmpty(req.getName())){
            criteria.andNameLike("%" + req.getName() + "%");
        }
        //从1开始。只对第一个遇到的select起作用
        PageHelper.startPage(req.getPage(),req.getSize());
        //持久层返回List<Ebook>需要转换成List<EbookResp>再返回Controller
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook>pageInfo=new PageInfo<>(ebookList);
        //一般前端分页组件只需要total，就会自己计算出pages
        LOG.info("总行数：{}",pageInfo.getTotal());
        LOG.info("总页数：{}",pageInfo.getPages());


        // List<EbookResp>respList = new ArrayList<>();
        // for (Ebook ebook : ebookList) {
        //    // EbookResp ebookResp=new EbookResp();
        //    // BeanUtils.copyProperties(ebook,ebookResp);
        //    //对象复制
        //     EbookResp ebookResp = CopyUtil.copy(ebook, EbookResp.class);
        //     respList.add(ebookResp);
        // }

        //列表复制
        List<EbookQueryResp> list = CopyUtil.copyList(ebookList, EbookQueryResp.class);

        PageResp<EbookQueryResp>pageResp=new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    /**
     * 保存
     * @param req
     */
    public void save(EbookSaveReq req) {
        Ebook ebook=CopyUtil.copy(req,Ebook.class);
        if(ObjectUtils.isEmpty(req.getId())){
            //新增
            ebook.setId(snowFlake.nextId());
            ebook.setDocCount(0);
            ebook.setViewCount(0);
            ebook.setVoteCount(0);
            ebookMapper.insert(ebook);
        }else {
            //更新
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }

    public void delete(Long id){
        ebookMapper.deleteByPrimaryKey(id);
    }
}
