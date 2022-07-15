package com.chenlf.community.controller;

import com.chenlf.community.entity.User;
import com.chenlf.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * 
 * @author ChenLF
 * @date 2022/07/15 21:17
 **/

@Controller
public class loginController {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/register", method= RequestMethod.GET)
    public String getRegisterPage(){
        return "site/register";
    }

    @RequestMapping(path = "/register", method=RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> register = userService.register(user);
        if (register == null || register.isEmpty()){
            model.addAttribute("msg","注册成功已经向你的邮箱发送了一封激活邮件,请尽快激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",register.get("usernameMsg"));
            model.addAttribute("passwordMsg",register.get("passwordMsg"));
            model.addAttribute("emailMsg",register.get("emailMsg"));
            return "site/register";
        }
    }

}
