package com.chenlf.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * 
 * @author ChenLF
 * @date 2022/10/02 10:14
 **/

@Data
public class Message {
    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    private int status;
    private Date createTime;
}
