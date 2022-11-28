package com.chenlf.community.controller;

import com.chenlf.community.entity.Comment;
import com.chenlf.community.entity.DiscussPost;
import com.chenlf.community.entity.Event;
import com.chenlf.community.event.EventProducer;
import com.chenlf.community.service.CommentService;
import com.chenlf.community.service.DiscussPostService;
import com.chenlf.community.util.HostHolder;
import com.chenlf.community.util.RedisKeyUtil;
import com.chenlf.community.util.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * 
 * @author ChenLF
 * @date 2022/10/01 17:15
 **/

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getVal().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        //出发评论系统消息
        Event event = new Event();
        event.setTopic(SystemConstants.TOPIC_COMMENT)
            .setUserId(comment.getUserId())
            .setEntityType(comment.getEntityType())
            .setEntityId(comment.getEntityId())
            .setData("postId",discussPostId);

        //评论的帖子给帖子作者发消息 评论地评论给评论地作者发
        if (comment.getEntityType() == SystemConstants.ENTITY_TYPE_POST){
            DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
            event.setEntityUserId(discussPost.getUserId());
        }else if (comment.getEntityType() == SystemConstants.ENTITY_TYPE_COMMENT){
            //回复 给回复对象发消息
            //单纯评论 给评论对象发消息
            if (comment.getTargetId() != 0){
                event.setEntityUserId(comment.getTargetId());
            }else{
                Comment target = commentService.findCommentById(comment.getEntityId());
                event.setEntityUserId(target.getUserId());
            }
        }
        eventProducer.fireEvent(event);

        //触发给帖子评论事件 存入ES
        if (comment.getEntityType() == SystemConstants.ENTITY_TYPE_POST){
            event = new Event();
            event.setTopic(SystemConstants.TOPIC_PUBLISH);
            event.setUserId(comment.getUserId());
            event.setEntityType(SystemConstants.ENTITY_TYPE_POST);
            event.setEntityId(discussPostId);
            event.setEntityUserId(0);
        }
        eventProducer.fireEvent(event);

        if (comment.getEntityType() == SystemConstants.ENTITY_TYPE_POST){
            String redisKey = RedisKeyUtil.getPostKey();
            redisTemplate.opsForSet().add(redisKey, discussPostId);
        }
        return "redirect:/discuss/detail/" + discussPostId;
    }
}
