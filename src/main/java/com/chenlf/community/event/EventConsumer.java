package com.chenlf.community.event;

import com.alibaba.fastjson.JSONObject;
import com.chenlf.community.entity.Event;
import com.chenlf.community.entity.Message;
import com.chenlf.community.service.MessageService;
import com.chenlf.community.util.SystemConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * kafka消息消费者
 * @author ChenLF
 * @date 2022/10/13 16:53
 **/

@Component
public class EventConsumer {

    @Autowired
    private MessageService messageService;

    private final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @KafkaListener(topics = {SystemConstants.TOPIC_COMMENT,SystemConstants.TOPIC_LIKE,SystemConstants.TOPIC_FOLLOW})
    public void messageHandler(ConsumerRecord record){
        if (record == null || record.value() == null){
            logger.error("kafka消息内容为空!");
            return ;
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);

        Message message = new Message();
        message.setFromId(SystemConstants.SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        content.put("userId",event.getUserId());

        if (!event.getData().isEmpty()){
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(),entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.insertMessage(message);
    }
}
