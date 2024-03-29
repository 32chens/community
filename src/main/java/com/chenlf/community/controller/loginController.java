package com.chenlf.community.controller;

import com.chenlf.community.config.KaptchaConfig;
import com.chenlf.community.entity.User;
import com.chenlf.community.service.UserService;
import com.chenlf.community.util.CommunityUtil;
import com.chenlf.community.util.RedisKeyUtil;
import com.chenlf.community.util.SystemConstants;
import com.google.code.kaptcha.Producer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author ChenLF
 * @date 2022/07/15 21:17
 **/

@Controller
public class loginController {

    private static final Logger logger = LoggerFactory.getLogger(loginController.class);

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;


    @RequestMapping(path = "/register", method= RequestMethod.GET)
    public String getRegisterPage(){
        return "site/register";
    }

    @RequestMapping(path = "/login", method= RequestMethod.GET)
    public String getLoginPage(){
        return "site/login";
    }

    /**
     * 注册
     * @param model
     * @param user
     * @return
     */
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

    /**
     * 激活账户 http://localhost:8080/community/activation/101/code
     * @param userId
     * @param code
     * @return
     */
    @RequestMapping(path = "/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
        int activation = userService.activation(userId, code);
        //激活失败
        if (activation == SystemConstants.USER_STATUS_UNACTIVE){
            model.addAttribute("msg","无效操作,您提供的激活码不正确!");
            model.addAttribute("target","/index");

        //重复激活
        }else if (activation == SystemConstants.USER_STATUS_REPEAT){
            model.addAttribute("msg","无效操作,该账户已激活过了!");
            model.addAttribute("target","/index");

        //激活成功
        }else{
            model.addAttribute("msg","激活成功,您的账户已可以正常使用!");
            model.addAttribute("target","/login");
        }
        return "/site/operate-result";
    }

    /**
     * 生成验证码
     * @param response
     * @param
     */
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response /*HttpSession session*/){
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        //将验证码存入session
//        session.setAttribute("kaptcha",text);
        String kaptchaOwner =  CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        String redisKey = RedisKeyUtil.getkaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);

        //将图片传给浏览器
        response.setContentType("image/png");
        try {
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image,"png",outputStream);
        } catch (IOException e) {
            logger.error("相应验证码失败"+e.getMessage());
        }
    }

    /**
     * 登录
     * @param username
     * @param password
     * @param code
     * @param rememberMe
     * @param model
     * @param //session
     * @param response
     * @return
     */
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberMe,
                        Model model, /*HttpSession session,*/ HttpServletResponse response,
                        @CookieValue("kaptchaOwner")String kaptchaOwner){
//        String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;
        if(StringUtils.isNotBlank(kaptchaOwner)){
            String redisKey = RedisKeyUtil.getkaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }

        if (!kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码错误!");
            return "/site/login";
        }

        int expiredTime = rememberMe ? SystemConstants.REMEMBER_LOGIN_EXPIRED : SystemConstants.DEFAULT_LOGIN_EXPIRED;
        Map<String, Object> map = userService.login(username, password, expiredTime);
        if (map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredTime);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket")String ticket){
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }

}
