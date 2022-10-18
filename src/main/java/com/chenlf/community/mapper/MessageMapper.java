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

    //新增消息
    int insertMessage(Message message);

    //修改消息状态
    int updateStatus(List<Integer> ids, int status);

    //查询某个主题下最新的通知
    Message selectLastNotice(int userId, String topic);

    //查询某个主题所含的数量
    int selectNoticeCount(int userId,String topic);

    //查询未读的通知数量
    int selectUnreadNoticeCount(int userId,String topic);

    //某个主题的通知列表
    List<Message> selectNotices(int userId, String topic, int offset, int limit);
}
