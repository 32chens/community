package com.chenlf.community.service;

import com.chenlf.community.entity.User;
import com.chenlf.community.mapper.UserMapper;
import com.chenlf.community.util.CommunityUtil;
import com.chenlf.community.util.MailClient;
import com.chenlf.community.util.SystemConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 
 * @author ChenLF
 * @date 2022/07/13 22:16
 **/

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private MailClient mailClient;

    @Resource
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domian;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int userId){
        return userMapper.selectById(userId);
    }

    /**
     * 注册
     * @param user
     * @return
     */
    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();
        if (user == null){
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","用户名不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空!");
            return map;
        }
        //验证账号
        User user1 = userMapper.selectByName(user.getUsername());
        if (user1 != null){
            map.put("usernameMsg","该账号已存在!");
            return map;
        }
        User user2 = userMapper.selectByEmail(user.getEmail());
        if (user2 != null){
            map.put("emailMsg","该邮箱已被使用!");
            return map;
        }
        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(SystemConstants.USER_TYPE_NOMAL);
        user.setStatus(SystemConstants.USER_STATUS_UNACTIVE);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        //发送邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //http://localhost:8080/community/activation/101/code
        String url = domian + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        //生成html模板内容
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;
    }

    /**
     * 激活账户
     * @param userId
     * @param code
     * @return
     */
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if (user == null){
            return SystemConstants.USER_STATUS_UNACTIVE;
        }
        if (user.getStatus() == SystemConstants.USER_STATUS_UNACTIVE){
            if (user.getActivationCode().equals(code)){
                userMapper.updateStatus(userId,SystemConstants.USER_STATUS_ACTIVE);
                return SystemConstants.USER_STATUS_ACTIVE;
            }else{
                return SystemConstants.USER_STATUS_UNACTIVE;
            }
        }else{
            return SystemConstants.USER_STATUS_REPEAT;
        }
    }

}
