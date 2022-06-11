package com.wxh.wiki.job;

import com.wxh.wiki.service.DocService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author wxh
 * @date 2022-06-11 13:58
 */
@Component
public class DocJob {

    @Autowired
    private DocService docService;

    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);

    /**
     * 每30秒更新电子书信息
     */
    @Scheduled(cron = "5/30 * * * * ?")
    public void cron() {
        docService.updateEbookInfo();
    }

}

