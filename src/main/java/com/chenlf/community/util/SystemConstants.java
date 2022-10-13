package com.chenlf.community.util;
/**
 * 常量类
 * @author ChenLF
 * @date 2022/07/15 21:49
 **/
 
public class SystemConstants {

    public static final int USER_TYPE_NOMAL = 0;

    /**
     * 账户未激活
     */
    public static final int USER_STATUS_UNACTIVE = 0;

    /**
     * 账户激活状态
     */
    public static final int USER_STATUS_ACTIVE = 1;

    /**
     * 账户重复激活
     */
    public static final int USER_STATUS_REPEAT = 2;

    /**
     * 默认登录令牌存活时间
     */
    public static final int DEFAULT_LOGIN_EXPIRED = 60 * 60 * 12;

    /**
     * 记住我登录令牌存活时间
     */
    public static final int REMEMBER_LOGIN_EXPIRED = 60 * 60 * 24 * 10;

    /**
     * 实体类型:帖子
     */
    public static final int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型: 评论
     */
    public static final int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型: 用户
     */
    public static final int ENTITY_TYPE_USER = 3;

    /**
     * Kafka主题: 评论
     */
    public static final String TOPIC_COMMENT = "comment";

    /**
     * Kafka主题: 点赞
     */
    public static final String TOPIC_LIKE = "like";

    /**
     * Kafka主题: 关注
     */
    public static final String TOPIC_FOLLOW = "follow";

    /**
     * 系统用户id
     */
    public static final int SYSTEM_USER_ID = 1;
}
