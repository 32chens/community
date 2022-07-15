package com.chenlf.community;

import com.chenlf.community.mapper.UserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
//@MapperScan("com.chenlf.community.mapper")
public class CommunityApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(CommunityApplication.class, args);
//        UserMapper userMapper = (UserMapper) run.getBean("userMapper");
//        System.out.println(userMapper);
    }

}
