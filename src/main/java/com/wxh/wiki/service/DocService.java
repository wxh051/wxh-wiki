package com.wxh.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wxh.wiki.domain.Content;
import com.wxh.wiki.domain.Doc;
import com.wxh.wiki.domain.DocExample;
import com.wxh.wiki.mapper.ContentMapper;
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

    @Autowired
    private ContentMapper contentMapper;

    public List<DocQueryResp> all(Long ebookId) {
        DocExample docExample = new DocExample();
        //必须这么写，这时ebookid=null,则搜不到任何文档
        //如果写成动态查询，有就查，没有就查出所有文档，不安全
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
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
        PageHelper.startPage(req.getPage(), req.getSize());
        //持久层返回List<Doc>需要转换成List<DocResp>再返回Controller
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        //一般前端分页组件只需要total，就会自己计算出pages
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        //列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    /**
     * 保存
     *
     * @param req
     */
    public void save(DocSaveReq req) {
        //doc.id==content.id
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            //新增
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);

            //这里不能再通过雪花算法获取了，而是直接去获取doc的ID，他两是一样的必须
            content.setId(doc.getId());
            contentMapper.insert(content);
        } else {
            //更新
            docMapper.updateByPrimaryKey(doc);
            //BLOB代表对一个大字段的更新
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if (count == 0) {
                contentMapper.insert(content);
            }
        }
    }

    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }

    //这里传入一个也string可以
    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    public String  findContent(Long id){
        Content content = contentMapper.selectByPrimaryKey(id);
        if(!ObjectUtils.isEmpty(content)){
            return content.getContent();
        }else{
            return "";
        }
    }
}


