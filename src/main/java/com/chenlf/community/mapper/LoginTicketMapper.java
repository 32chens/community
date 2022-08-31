package com.chenlf.community.mapper;

import com.chenlf.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoginTicketMapper {

    LoginTicket selectByTicket(@Param("ticket") String ticket);

    int insertTicket(LoginTicket loginTicket);

    int updateTicket(@Param("ticket") String ticket, @Param("status") int status);

}
