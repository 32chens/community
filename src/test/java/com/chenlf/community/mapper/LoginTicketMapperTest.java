package com.chenlf.community.mapper;

import com.chenlf.community.CommunityApplication;
import com.chenlf.community.entity.LoginTicket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@SpringBootTest(classes = {CommunityApplication.class}) //启动类的class,
@ContextConfiguration(classes = LoginTicketMapperTest.class)
class LoginTicketMapperTest {

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    void selectByTicket() {
        System.out.println(loginTicketMapper.selectByTicket("abc"));
    }

    @Test
    void insertTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setStatus(0);
        loginTicket.setTicket("abc");
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        this.loginTicketMapper.insertTicket(loginTicket);
    }

    @Test
    void updateTicket() {
        loginTicketMapper.updateTicket("abc", 1);
    }
}