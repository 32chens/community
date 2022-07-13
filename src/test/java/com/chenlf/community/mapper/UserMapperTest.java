package com.chenlf.community.mapper;

import com.chenlf.community.CommunityApplication;
import com.chenlf.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(classes = {CommunityApplication.class}) //启动类的class,
@ContextConfiguration(classes = UserMapperTest.class)
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void selectById() {
        User user = userMapper.selectById(101);
        System.out.println(user);
    }
}