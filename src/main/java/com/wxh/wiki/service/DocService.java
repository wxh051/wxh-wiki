package com.wxh.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wxh.wiki.domain.Doc;
import com.wxh.wiki.domain.DocExample;
import com.wxh.wiki.mapper.DocMapper;
import com.wxh.wiki.req.DocQueryReq;
import com.wxh.wiki.req.DocSaveReq;
import com.wxh.wiki.resp.DocQueryResp;
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
public class DocService {
    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);


    @Autowired
    private DocMapper docMapper;

    @Autowired
    private SnowFlake snowFlake;

    public List<DocQueryResp> all() {
        DocExample docExample = new DocExample();
        //排序
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);

        //列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }

    public PageResp<DocQueryResp> list(DocQueryReq req) {
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");

        DocExample.Criteria criteria = docExample.createCriteria();

        //从1开始。只对第一个遇到的select起作用
        PageHelper.startPage(req.getPage(),req.getSize());
        //持久层返回List<Doc>需要转换成List<DocResp>再返回Controller
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc>pageInfo=new PageInfo<>(docList);
        //一般前端分页组件只需要total，就会自己计算出pages
        LOG.info("总行数：{}",pageInfo.getTotal());
        LOG.info("总页数：{}",pageInfo.getPages());

        //列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp>pageResp=new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    /**
     * 保存
     * @param req
     */
    public void save(DocSaveReq req) {
        Doc doc=CopyUtil.copy(req,Doc.class);
        if(ObjectUtils.isEmpty(req.getId())){
            //新增
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);
        }else {
            //更新
            docMapper.updateByPrimaryKey(doc);
        }
    }

    public void delete(Long id){
        docMapper.deleteByPrimaryKey(id);
    }

    //这里传入一个也string可以
    public void delete(List<String> ids){
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }
}
