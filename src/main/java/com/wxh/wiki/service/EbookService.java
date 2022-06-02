package com.wxh.wiki.service;

import com.wxh.wiki.domain.Ebook;
import com.wxh.wiki.mapper.EbookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wxh
 * @date 2022-06-02 17:00
 */
@Service
public class EbookService {

    @Autowired
    private EbookMapper ebookMapper;

    public List<Ebook>list(){
        return ebookMapper.selectByExample(null);
    }
}
