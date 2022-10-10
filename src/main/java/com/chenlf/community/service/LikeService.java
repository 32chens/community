package com.chenlf.community.service;

import com.chenlf.community.util.RedisKeyUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
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
    public void like(int userId,int entityType, int entityId,int entityUserId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisEntityLikeKey = RedisKeyUtil.getRedisEntityLikeKey(entityType, entityId);
                String redisUserLikeKey = RedisKeyUtil.getRedisUserLikeKey(entityUserId);
                Boolean isMember = redisTemplate.opsForSet().isMember(redisEntityLikeKey, userId);
                operations.multi();
                if (isMember){
                    redisTemplate.opsForSet().remove(redisEntityLikeKey,userId);
                    redisTemplate.opsForValue().decrement(redisUserLikeKey);
                }else{
                    redisTemplate.opsForSet().add(redisEntityLikeKey,userId);
                    redisTemplate.opsForValue().increment(redisUserLikeKey);
                }

                return operations.exec();
            }
        });

    }

    /**
     * 获取点赞数量
     * @param entityType
     * @param entityId
     * @return
     */
    public long getLikeCount(int entityType, int entityId){
        String redisLikeKey = RedisKeyUtil.getRedisEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(redisLikeKey);
    }

    /**
     * 获取点赞状态 1已赞 0未赞
     * @param entityType
     * @param entityId
     * @return
     */
    public int getLikeStatus(int userId, int entityType, int entityId){
        String redisLikeKey = RedisKeyUtil.getRedisEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(redisLikeKey, userId) ? 1 : 0;
    }

    /**
     * 查询某用户收到的赞
     * @param userId
     * @return
     */
    public int getUserLikeCount(int userId){
        String redisUserLikeKey = RedisKeyUtil.getRedisUserLikeKey(userId);
        Integer likeCount = (Integer) redisTemplate.opsForValue().get(redisUserLikeKey);
        return likeCount == null ? 0 : likeCount.intValue();
    }

}
