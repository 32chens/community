package com.chenlf.community.controller;

import com.chenlf.community.entity.User;
import com.chenlf.community.service.FollowService;
import com.chenlf.community.util.CommunityUtil;
import com.chenlf.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author ChenLF
 * @date 2022/10/10 21:27
 **/

@Controller
public class FollowController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FollowService followService;

    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId){
        User user = hostHolder.getVal();

        followService.follow(user.getId(), entityType, entityId);
        return CommunityUtil.getJSONString(0,"已关注");
    }

    @RequestMapping(path = "/unFollow", method = RequestMethod.POST)
    @ResponseBody
    public String unFollow(int entityType, int entityId){
        User user = hostHolder.getVal();

        followService.unFollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJSONString(0,"已取消关注");
    }


}
