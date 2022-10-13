package com.chenlf.community.controller;

import com.chenlf.community.entity.Event;
import com.chenlf.community.entity.Page;
import com.chenlf.community.entity.User;
import com.chenlf.community.event.EventProducer;
import com.chenlf.community.service.FollowService;
import com.chenlf.community.service.UserService;
import com.chenlf.community.util.CommunityUtil;
import com.chenlf.community.util.HostHolder;
import com.chenlf.community.util.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

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

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId){
        User user = hostHolder.getVal();

        followService.follow(user.getId(), entityType, entityId);

        Event event = new Event();
        event.setTopic(SystemConstants.TOPIC_FOLLOW)
                .setUserId(hostHolder.getVal().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0,"已关注");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unFollow(int entityType, int entityId){
        User user = hostHolder.getVal();

        followService.unFollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJSONString(0,"已取消关注");
    }

    /**
     * 查询目标用户的关注列表
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId")int userId, Page page, Model model){
        //目标用户
        User user = userService.findUserById(userId);
        if (user == null){
            throw new RuntimeException("该用户不存在!");
        }
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId, SystemConstants.ENTITY_TYPE_USER));
        model.addAttribute("user", user);

        //目标用户关注的用户的信息
        List<Map<String, Object>> userList = followService.findFollwees(userId, page.getOffset(), page.getLimit());
        if (userList != null){
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed",this.hasFollowed(u.getId()));
            }
            model.addAttribute("users", userList);
        }
        return "/site/followee";
    }

    /**
     * 查询目标用户的粉丝列表
     * @param userId 目标用户id
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId")int userId, Page page, Model model){
        //目标用户
        User user = userService.findUserById(userId);
        if (user == null){
            throw new RuntimeException("该用户不存在!");
        }
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(SystemConstants.ENTITY_TYPE_USER, userId));

        model.addAttribute("user", user);
        List<Map<String, Object>> userList = followService.findFollwers(userId, page.getOffset(), page.getLimit());
        if (userList != null){
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed",this.hasFollowed(u.getId()));
            }
            model.addAttribute("users", userList);
        }
        return "/site/follower";
    }

    /**
     * 判断当前用户是否关注了它
     * @param userId
     * @return
     */
    private boolean hasFollowed(int userId){
        if (hostHolder.getVal() == null){
            return false;
        }
        return followService.hasFollowed(hostHolder.getVal().getId(), SystemConstants.ENTITY_TYPE_USER, userId);
    }

}
