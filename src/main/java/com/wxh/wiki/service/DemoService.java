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
     *可以直接使用@resource，如果要用@Autowired。需在mapper上添加@Repository注解
     */
    @Autowired
    private DemoMapper demoMapper;

    public List<Demo>list(){
        return demoMapper.selectByExample(null);
    }
}
