package com.chenlf.community.controller;

import com.chenlf.community.entity.DiscussPost;
import com.chenlf.community.entity.User;
import com.chenlf.community.service.DiscussPostService;
import com.chenlf.community.util.CommunityUtil;
import com.chenlf.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * 
 * @author ChenLF
 * @date 2022/09/19 23:36
 **/

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getVal();
        if(user == null){
            return CommunityUtil.getJSONString(403,"你还没有登录哦!~");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle(title);
        discussPost.setUserId(user.getId());
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.insertDiscussPost(discussPost);

        return CommunityUtil.getJSONString(0,"操作成功");
    }
}
