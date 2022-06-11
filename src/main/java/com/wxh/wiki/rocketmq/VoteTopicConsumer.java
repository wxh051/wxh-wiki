package com.wxh.wiki.rocketmq;

/**
 * @author wxh
 * @date 2022-06-11 21:20
 */
/*
@Service
@RocketMQMessageListener(consumerGroup = "default", topic = "VOTE_TOPIC")
public class VoteTopicConsumer implements RocketMQListener<MessageExt> {

    private static final Logger LOG = LoggerFactory.getLogger(VoteTopicConsumer.class);

    @Autowired
    public WebSocketServer webSocketServer;

    @Override
    public void onMessage(MessageExt messageExt) {
        byte[] body = messageExt.getBody();
        LOG.info("ROCKETMQ收到消息：{}", new String(body));
        webSocketServer.sendInfo(new String(body));
    }
}
*/
