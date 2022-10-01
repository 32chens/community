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
    private int targetId;
    private String content;
    private int status;
    private Date createTime;
}
