package com.chenlf.community.mapper;

import com.chenlf.community.CommunityApplication;
import com.chenlf.community.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {CommunityApplication.class}) //启动类的class,
@ContextConfiguration(classes = MessageMapperTest.class)
class MessageMapperTest {

    @Autowired
    private MessageMapper messageMapper;

    @Test
    void selectConversations() {
        List<Message> list = messageMapper.selectConversations(111, 0, 20);
        for (Message message : list) {
            System.out.println(message);
        }
    }

    @Test
    void selectconversationCount() {
        int i = messageMapper.selectconversationCount(111);
        System.out.println(i);
    }

    @Test
    void selectLetters() {
        List<Message> list = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : list) {
            System.out.println(message);
        }
    }

    @Test
    void selectLetterCount() {
        System.out.println(messageMapper.selectLetterCount("111_112"));
    }

    @Test
    void selectLetterUnreadCount() {
        System.out.println(messageMapper.selectLetterUnreadCount(131, "111_131"));
    }
}