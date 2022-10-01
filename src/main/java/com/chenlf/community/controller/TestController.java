package com.chenlf.community.controller;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.HttpCookie;

/**
 * 
 * @author ChenLF
 * @date 2022/08/30 22:33
 **/

@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/setCookie")
    public String setCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("code", "setcookie");
        cookie.setPath("/community/test");
        cookie.setMaxAge(60 * 10);
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping("/getCookie")
    public String getCookie(@CookieValue("code") String cookie){
        System.out.println(cookie);
        return "get cookie";
    }

    @RequestMapping("/setSession")
    public String setSession(HttpSession session){
        session.setAttribute("name","chen");
        return "set session";
    }

    @RequestMapping("/getSession")
    public String getSession(HttpSession session){

        return (String) session.getAttribute("name");
    }
}
