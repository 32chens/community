package com.chenlf.community.entity;

import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * (DiscussPost)实体类
 *
 * @author makejava
 * @since 2022-07-11 16:34:52
 */
@Data
public class DiscussPost implements Serializable {
    private static final long serialVersionUID = 653954247782732132L;
    
    private Integer id;
    
    private String userId;
    
    private String title;
    
    private String content;
    /**
     * 0-普通; 1-置顶;
     */
    private Integer type;
    /**
     * 0-正常; 1-精华; 2-拉黑;
     */
    private Integer status;
    
    private Date createTime;
    
    private Integer commentCount;
    
    private Object score;

}

