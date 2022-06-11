package com.wxh.wiki.mapper;

import com.wxh.wiki.resp.StatisticResp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author wxh
 * @date 2022-06-11 22:50
 */
@Mapper
public interface EbookSnapshotMapperCust {

    public void genSnapshot();

    List<StatisticResp> getStatistic();

}

