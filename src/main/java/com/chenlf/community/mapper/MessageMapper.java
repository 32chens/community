package com.chenlf.community.mapper;

import com.chenlf.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 
 * @author ChenLF
 * @date 2022/10/02 10:16
 **/

@Mapper
public interface MessageMapper {

    //查询当前用户的绘画列表，每个回话返回最新的一条
    List<Message> selectConversations(int userId, int offset, int limit);

    //查询当前用户的绘画数量
    int selectconversationCount(int userId);

    //查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    //查询某个会话所包含的私信数量
    int selectLetterCount(String conversatioinId);

    //查询未读私信的数量
    int selectLetterUnreadCount(int userId, String conversationId);

}
