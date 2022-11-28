package com.chenlf.community.controller;

import com.chenlf.community.entity.*;
import com.chenlf.community.event.EventProducer;
import com.chenlf.community.service.CommentService;
import com.chenlf.community.service.DiscussPostService;
import com.chenlf.community.service.LikeService;
import com.chenlf.community.service.UserService;
import com.chenlf.community.util.CommunityUtil;
import com.chenlf.community.util.HostHolder;
import com.chenlf.community.util.RedisKeyUtil;
import com.chenlf.community.util.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

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
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    /**
     * 发布帖子
     * @param title
     * @param content
     * @return
     */
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

        //发帖事件 存入ES
        Event event = new Event();
        event.setTopic(SystemConstants.TOPIC_PUBLISH);
        event.setUserId(user.getId());
        event.setEntityType(SystemConstants.ENTITY_TYPE_POST);
        event.setEntityId(discussPost.getId());
        eventProducer.fireEvent(event);

        String redisKey = RedisKeyUtil.getPostKey();
        redisTemplate.opsForSet().add(redisKey, discussPost.getId());

        return CommunityUtil.getJSONString(0,"操作成功");
    }


    /**
     * 查询帖子详情
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(path = "detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId")int id, Model model, Page page){
        DiscussPost discussPost = discussPostService.findDiscussPostById(id);
        model.addAttribute("post",discussPost);
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user",user);
        // 点赞数量
        long likeCount = likeService.getLikeCount(SystemConstants.ENTITY_TYPE_POST, id);
        model.addAttribute("likeCount", likeCount);
        // 点赞状态
        int likeStatus = hostHolder.getVal() == null ? 0 :
                likeService.getLikeStatus(hostHolder.getVal().getId(), SystemConstants.ENTITY_TYPE_POST, id);
        model.addAttribute("likeStatus", likeStatus);

        //评论的分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/"+id);
        page.setRows(discussPost.getCommentCount());

        //给帖子的评论
        List<Comment> commentList = commentService.findCommentByEntity(
                SystemConstants.ENTITY_TYPE_POST,
                discussPost.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVOList = new ArrayList<>();
        if (commentList != null){
            for (Comment comment: commentList) {
                Map<String, Object> commentVO = new HashMap<>();
                commentVO.put("comment",comment);
                commentVO.put("user",userService.findUserById(comment.getUserId()));
                // 点赞数量
                likeCount = likeService.getLikeCount(SystemConstants.ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("likeCount", likeCount);
                // 点赞状态
                likeStatus = hostHolder.getVal() == null ? 0 :
                        likeService.getLikeStatus(hostHolder.getVal().getId(), SystemConstants.ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("likeStatus", likeStatus);
                //评论的评论
                List<Comment> replyList = commentService.findCommentByEntity(
                        SystemConstants.ENTITY_TYPE_COMMENT,
                        comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null){
                    for (Comment reply: replyList) {
                        Map<String, Object> replyVO = new HashMap<>();
                        replyVO.put("reply",reply);
                        replyVO.put("user", userService.findUserById(reply.getUserId()));
                        // 点赞数量
                        likeCount = likeService.getLikeCount(SystemConstants.ENTITY_TYPE_COMMENT, reply.getId());
                        replyVO.put("likeCount", likeCount);
                        // 点赞状态
                        likeStatus = hostHolder.getVal() == null ? 0 :
                                likeService.getLikeStatus(hostHolder.getVal().getId(), SystemConstants.ENTITY_TYPE_COMMENT, reply.getId());
                        replyVO.put("likeStatus", likeStatus);
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVO.put("target", target);
                        replyVoList.add(replyVO);
                    }
                }
                commentVO.put("replys", replyVoList);

                //回复数量
                int replyCount = commentService.findCommentCount(SystemConstants.ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("replyCount", replyCount);

                commentVOList.add(commentVO);
            }
        }

        model.addAttribute("comments",commentVOList);
        return "/site/discuss-detail";
    }

    //置顶
    @RequestMapping(path = "/top", method = RequestMethod.POST)
    @ResponseBody
    public String setTop(int id){
        discussPostService.updateType(id,1);
        Event event = new Event();
        event.setTopic(SystemConstants.TOPIC_PUBLISH);
        event.setUserId(hostHolder.getVal().getId());
        event.setEntityType(SystemConstants.ENTITY_TYPE_POST);
        event.setEntityId(id);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0);
    }

    //加精
    @RequestMapping(path = "/wonderful", method = RequestMethod.POST)
    @ResponseBody
    public String setWonderful(int id){
        discussPostService.updateStatus(id,1);
        Event event = new Event();
        event.setTopic(SystemConstants.TOPIC_PUBLISH);
        event.setUserId(hostHolder.getVal().getId());
        event.setEntityType(SystemConstants.ENTITY_TYPE_POST);
        event.setEntityId(id);
        eventProducer.fireEvent(event);

        String redisKey = RedisKeyUtil.getPostKey();
        redisTemplate.opsForSet().add(redisKey, id);
        return CommunityUtil.getJSONString(0);
    }

    //删除
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String setDelete(int id){
        discussPostService.updateStatus(id,1);
        //触发删贴事件
        Event event = new Event();
        event.setTopic(SystemConstants.TOPIC_DELETE);
        event.setUserId(hostHolder.getVal().getId());
        event.setEntityType(SystemConstants.ENTITY_TYPE_POST);
        event.setEntityId(id);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0);
    }


}
