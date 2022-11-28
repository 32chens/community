package com.chenlf.community.quartz;

import com.chenlf.community.entity.DiscussPost;
import com.chenlf.community.service.CommentService;
import com.chenlf.community.service.DiscussPostService;
import com.chenlf.community.service.ElasticSearchService;
import com.chenlf.community.service.LikeService;
import com.chenlf.community.util.RedisKeyUtil;
import com.chenlf.community.util.SystemConstants;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author ChenLF
 * @date 2022/11/28 21:25
 **/
 
public class PostScoreRefreshJob implements Job {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ElasticSearchService elasticSearchService;


    @Autowired
    private LikeService likeService;

    @Autowired
    private DiscussPostService discussPostService;

    private static final Date epoch;

    private Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").parse("2014-08-01 00-00-00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化论坛纪元失败");
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);
        if (operations.size() == 0){
            logger.info("[任务取消] 没有分数更新的帖子");
            return ;
        }
        logger.info("[任务开始] 正在更新帖子分数");
        while (operations.size() > 0){
            this.refresh((Integer)operations.pop());
        }
        logger.info("[任务结束] 更新帖子分数结束");
    }

    private void refresh(Integer postId) {
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if (post == null){
            logger.error("该帖子不存在："+ postId);
            return ;
        }
        boolean wonderful = post.getStatus() == 1;
        int comentCount = post.getCommentCount();
        int likeCount = (int) likeService.getLikeCount(SystemConstants.ENTITY_TYPE_POST, postId);
        //权重
        double w = (wonderful ? 75 : 0) + comentCount *10 + likeCount + 2;
        //分数
        double score =  Math.log10(Math.max(w, 1)) + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);
        discussPostService.updateScore(postId, score);
        post.setScore(score);
        elasticSearchService.saveDiscussPost(post);
    }
}
