package com.chenlf.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author ChenLF
 * @date 2022/09/01 23:05
 **/
 
public class CookieUtil {
    public static String getValue(HttpServletRequest request, String ticket){
        if (request == null || ticket == null){
            throw new IllegalArgumentException("获取cookie值时，参数不能为空");
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(ticket)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
