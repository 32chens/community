package com.chenlf.community.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.io.Serializable;

/**
 * (DiscussPost)实体类
 *
 * @author makejava
 * @since 2022-07-11 16:34:52
 */
@Data
@Document(indexName = "discusspost")
public class DiscussPost implements Serializable {
    private static final long serialVersionUID = 653954247782732132L;
    @Id
    private int id;

    @Field(type = FieldType.Integer)
    private int userId;

    // 互联网校招
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    @Field(type = FieldType.Integer)
    private int type;

    @Field(type = FieldType.Integer)
    private int status;

//    @Field(type = FieldType.Date,format = DateFormat.date_optional_time)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd'T'HH:mm:ss.SSSZ",timezone="GMT+8")
    @Field(type = FieldType.Date, format = DateFormat.epoch_millis,pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd",timezone="GMT+8")
    private Date createTime;

    @Field(type = FieldType.Integer)
    private int commentCount;

    @Field(type = FieldType.Double)
    private double score;

}

