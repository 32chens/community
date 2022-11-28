package com.chenlf.community.mapper;

import com.chenlf.community.CommunityApplication;
import com.chenlf.community.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {CommunityApplication.class}) //启动类的class,
@ContextConfiguration(classes = DiscussPostMapperTest.class)
class DiscussPostMapperTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    void selectDiscussPosts() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10, 0);
        for (DiscussPost discussPost : discussPosts){
            System.out.println(discussPost);
        }
    }

    @Test
    void selectDiscussPostRows() {
        int r = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(r);
    }
}