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
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";
    private static final String PREFIX_Post = "post";

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

    //验证吗
    public static String getkaptchaKey(String kaptchaOwner){
        return PREFIX_KAPTCHA + SPLIT + kaptchaOwner;
    }

    //登录凭证
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    //用户
    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }

    //单日uv
    public static String getUVKey(String date){
        return PREFIX_UV + SPLIT + date;
    }

    //区间uv
    public static String getUVKey(String startDate, String endDate){
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    //单日dau
    public static String getDAUKey(String date){
        return PREFIX_DAU + SPLIT + date;
    }

    //区间dau
    public static String getDAUKey(String startDate, String endDate){
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    //帖子
    public static String getPostKey(){
        return PREFIX_Post + SPLIT + "score";
    }
}
