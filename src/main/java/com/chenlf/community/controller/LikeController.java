package com.chenlf.community.controller;

import com.chenlf.community.entity.User;
import com.chenlf.community.service.LikeService;
import com.chenlf.community.util.CommunityUtil;
import com.chenlf.community.util.HostHolder;
import com.chenlf.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author ChenLF
 * @date 2022/10/10 17:02
 **/

@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId){
//        String redisLikeKey = RedisKeyUtil.getRedisEntityLikeKey(entityType, entityId);
        User user = hostHolder.getVal();
        if (user == null){
            return CommunityUtil.getJSONString(403,"用户未登录");
        }
        int userId = user.getId();
        //点赞
        likeService.like(userId,entityType,entityId,entityUserId);
        //数量
        long likeCount = likeService.getLikeCount(entityType, entityId);
        //状态
        int likeStatus = likeService.getLikeStatus(userId, entityType, entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        return CommunityUtil.getJSONString(0,null, map);
    }

}
