package com.chenlf.community.util;
/**
 * 
 * @author ChenLF
 * @date 2022/10/10 16:51
 **/
 
public class RedisKeyUitl {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    public static String getRedisLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

}
