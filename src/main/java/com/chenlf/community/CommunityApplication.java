package com.chenlf.community;

import com.chenlf.community.mapper.UserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.chenlf.community.mapper.elasticsearch")
//@MapperScan("com.chenlf.community.mapper")
public class CommunityApplication {

    @PostConstruct
    public void init(){
        //解决redis的netty和elasticsearch的netty的冲突
        //see
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(CommunityApplication.class, args);
//        UserMapper userMapper = (UserMapper) run.getBean("userMapper");
//        System.out.println(userMapper);
    }

}
