package com.wxh.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wxh.wiki.domain.Content;
import com.wxh.wiki.domain.ContentExample;
import com.wxh.wiki.domain.Doc;
import com.wxh.wiki.domain.DocExample;
import com.wxh.wiki.exception.BusinessException;
import com.wxh.wiki.exception.BusinessExceptionCode;
import com.wxh.wiki.mapper.ContentMapper;
import com.wxh.wiki.mapper.DocMapper;
import com.wxh.wiki.mapper.DocMapperCust;
import com.wxh.wiki.req.DocQueryReq;
import com.wxh.wiki.req.DocSaveReq;
import com.wxh.wiki.resp.DocQueryResp;
import com.wxh.wiki.resp.PageResp;
import com.wxh.wiki.util.CopyUtil;
import com.wxh.wiki.util.RedisUtil;
import com.wxh.wiki.util.RequestContext;
import com.wxh.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private DocMapperCust docMapperCust;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private WsService wsService;

    // @Autowired
    // private RocketMQTemplate rocketMQTemplate;

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
    @Transactional
    public void save(DocSaveReq req) {
        //doc.id==content.id
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            //新增
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
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
        //这里修改了docexample类中的andIdIn方法参数为String类型
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);

        //删除文档内容
        ContentExample contentExample = new ContentExample();
        ContentExample.Criteria criteria1 = contentExample.createCriteria();
        criteria1.andIdIn(ids);
        contentMapper.deleteByExample(contentExample);
    }

    public String findContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        //文档阅读数加一
        docMapperCust.increaseViewCount(id);
        if (!ObjectUtils.isEmpty(content)) {
            return content.getContent();
        } else {
            return "";
        }
    }

    /**
     * 点赞
     */
    public void vote(Long id) {
        // docMapperCust.increaseVoteCount(id);
        // 远程IP+doc.id作为key，24小时内不能重复
        String ip = RequestContext.getRemoteAddr();
        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip, 5000)) {
            docMapperCust.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }

        //推送消息
        //这里获取名字采用从数据库查，也可以通过前端传过来
        Doc docDb = docMapper.selectByPrimaryKey(id);
        String logId = MDC.get("LOG_ID");
        wsService.sendInfo("【" + docDb.getName() + "】被点赞！", logId);
        //rocketMQTemplate.convertAndSend("VOTE_TOPIC","【" + docDb.getName() + "】被点赞！");
    }

    //在这里调用定时器需要的SQL语句，定时器通过service调用。避免job直接调用mapper
    public void updateEbookInfo() {
        docMapperCust.updateEbookInfo();
    }

}


