package com.chenlf.community.util;
/**
 * 
 * @author ChenLF
 * @date 2022/10/10 16:51
 **/
 
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";

    //某个实体的赞
    public static String getRedisEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    //某个用户收到的赞
//    like:user:userId -> int
    public static String getRedisUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //关注某个实体
    //followee:userId:entityType -> zset(entityId,now)
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    //某个实体的粉丝
    //follower:entityType:entityId -> zset(userId,now)
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

}
