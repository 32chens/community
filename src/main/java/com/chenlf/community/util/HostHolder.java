package com.chenlf.community.util;

import com.chenlf.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 
 * @author ChenLF
 * @date 2022/09/01 23:17
 **/

@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setVal(User user){
        users.set(user);
    }

    public User getVal(){
        return users.get();
    }

    public void clean(){
        users.remove();
    }
}
