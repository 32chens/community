package com.chenlf.community.service;

import com.chenlf.community.util.HostHolder;
import com.chenlf.community.util.RedisKeyUitl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 点赞
 * @author ChenLF
 * @date 2022/10/10 16:54
 **/

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 点赞功能
     * redis存用户id， 无则点赞  有则取消
     * @param entityType
     * @param entityId
     */
    public void like(int userId,int entityType, int entityId){
        String redisLikeKey = RedisKeyUitl.getRedisLikeKey(entityType, entityId);
        Boolean isMember = redisTemplate.opsForSet().isMember(redisLikeKey, userId);
        if (isMember){
            redisTemplate.opsForSet().remove(redisLikeKey, userId);
        }else{
            redisTemplate.opsForSet().add(redisLikeKey, userId);
        }
    }

    /**
     * 获取点赞数量
     * @param entityType
     * @param entityId
     * @return
     */
    public long getLikeCount(int entityType, int entityId){
        String redisLikeKey = RedisKeyUitl.getRedisLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(redisLikeKey);
    }

    /**
     * 获取点赞状态 1已赞 0未赞
     * @param entityType
     * @param entityId
     * @return
     */
    public int getLikeStatus(int userId, int entityType, int entityId){
        String redisLikeKey = RedisKeyUitl.getRedisLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(redisLikeKey, userId) ? 1 : 0;
    }

}
