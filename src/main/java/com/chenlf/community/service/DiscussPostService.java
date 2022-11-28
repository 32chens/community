package com.chenlf.community.service;

import com.chenlf.community.entity.DiscussPost;
import com.chenlf.community.mapper.DiscussPostMapper;
import com.chenlf.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 
 * @author ChenLF
 * @date 2022/07/12 23:38
 **/

@Service
public class DiscussPostService {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Resource
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit,int orderModel){
        return discussPostMapper.selectDiscussPosts(userId,offset,limit,orderModel);
    }

    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int insertDiscussPost(DiscussPost discussPost){
        if (discussPost == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));
        return discussPostMapper.insertDiscussPost(discussPost);
    }

    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount){
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    public int updateType(int id,int type){
        return discussPostMapper.updateType(id,type);
    }

    public int updateStatus(int id, int status){
        return discussPostMapper.updateStatus(id,status);
    }

    public int updateScore(int id, double score){
        return discussPostMapper.updateScore(id, score);
    }
}
