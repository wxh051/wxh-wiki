package com.wxh.wiki.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * @author wxh
 * @date 2022-06-11 22:50
 */
@Mapper
public interface EbookSnapshotMapperCust {

    public void genSnapshot();
}

