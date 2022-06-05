package com.wxh.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wxh.wiki.domain.Ebook;
import com.wxh.wiki.domain.EbookExample;
import com.wxh.wiki.mapper.EbookMapper;
import com.wxh.wiki.req.EbookReq;
import com.wxh.wiki.resp.EbookResp;
import com.wxh.wiki.util.CopyUtil;
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

    public List<EbookResp> list(EbookReq req) {
        EbookExample ebookExample = new EbookExample();
        //相当于where条件
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        //动态SQL
        if(!ObjectUtils.isEmpty(req.getName())){
            criteria.andNameLike("%" + req.getName() + "%");
        }
        //从1开始。只对第一个遇到的select起作用
        PageHelper.startPage(1,3);
        //持久层返回List<Ebook>需要转换成List<EbookResp>再返回Controller
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook>pageInfo=new PageInfo<>(ebookList);
        //一般前端分页组件只需要total，就会自己计算出pages
        LOG.info("总行数：{}",pageInfo.getTotal());


        // List<EbookResp>respList = new ArrayList<>();
        // for (Ebook ebook : ebookList) {
        //    // EbookResp ebookResp=new EbookResp();
        //    // BeanUtils.copyProperties(ebook,ebookResp);
        //    //对象复制
        //     EbookResp ebookResp = CopyUtil.copy(ebook, EbookResp.class);
        //     respList.add(ebookResp);
        // }

        //列表复制
        List<EbookResp> list = CopyUtil.copyList(ebookList, EbookResp.class);

        return list;
    }
}
