package com.wxh.wiki.service;

import com.wxh.wiki.mapper.EbookSnapshotMapperCust;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wxh
 * @date 2022-06-11 22:49
 */
@Service
public class EbookSnapshotService {

    @Autowired
    private EbookSnapshotMapperCust ebookSnapshotMapperCust;

    public void genSnapshot() {
        ebookSnapshotMapperCust.genSnapshot();
    }

}
