package com.chenlf.community.service;

import com.chenlf.community.entity.User;
import com.chenlf.community.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 
 * @author ChenLF
 * @date 2022/07/13 22:16
 **/

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    public User findUserById(int userId){
        return userMapper.selectById(userId);
    }

}
