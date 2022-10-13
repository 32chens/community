package com.chenlf.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * 
 * @author ChenLF
 * @date 2022/10/01 14:55
 **/

@Data
public class Comment {
    private int id;
    private int userId;
    private int entityType;
    private int entityId;
    private int targetId; //回复的用户的id  0表示没有回复,只是单纯的给评论评论
    private String content;
    private int status;
    private Date createTime;
}
