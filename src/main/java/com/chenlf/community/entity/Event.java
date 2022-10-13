package com.chenlf.community.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author ChenLF
 * @date 2022/10/13 16:27
 **/
@Data
public class Event {
    private String topic;
    private int userId; //事件发起人id
    private int entityType; //事件实体类型
    private int entityId;   //具体事件实体id
    private int entityUserId;   //消息接受人id
    private Map<String,Object> data = new HashMap<>();

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Event setData(String key, Object value) {
        this.data.put(key,value);
        return this;
    }
}
