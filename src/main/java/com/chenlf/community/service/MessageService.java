package com.chenlf.community.service;

import com.chenlf.community.entity.Message;
import com.chenlf.community.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author ChenLF
 * @date 2022/10/02 10:47
 **/

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    public List<Message> findConversations(int userId, int offset, int limit){
        return messageMapper.selectConversations(userId,offset,limit);
    }

    public int findConversationCount(int userId){
        return messageMapper.selectconversationCount(userId);
    }

    public List<Message> findLetters(String conversationId, int offset, int limit){
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    public int findLettersCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    public int findLetterUnreadCount(int userId, String conversationId){
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

}
