package com.wxh.wiki.service;

import com.wxh.wiki.domain.Demo;
import com.wxh.wiki.mapper.DemoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wxh
 * @date 2022-06-02 17:00
 */
@Service
public class DemoService {

    /**
     *可以直接使用@resource，@Resource是JDK的
     * 如果要用@Autowired。需在mapper上添加@Repository注解。
     * 因为@Autowired是Spring的，编辑器里的Spring插件会校验TestMapper有没有扫描注解，
     * 但实际上所有的mapper是统一在启动类里统一扫描到，所以只是编辑器校验错，实际可以运行
     */
    @Autowired
    private DemoMapper demoMapper;

    public List<Demo>list(){
        return demoMapper.selectByExample(null);
    }
}
