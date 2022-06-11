package com.wxh.wiki.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wxh
 */
@Mapper
public interface DocMapperCust {

    public void increaseViewCount(@Param("id") Long id);
}