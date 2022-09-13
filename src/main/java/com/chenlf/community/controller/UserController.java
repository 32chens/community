package com.chenlf.community.controller;

import com.chenlf.community.entity.User;
import com.chenlf.community.service.UserService;
import com.chenlf.community.util.CommunityUtil;
import com.chenlf.community.util.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * @author ChenLF
 * @date 2022/09/13 20:57
 **/

@Controller
@RequestMapping("/user")
public class UserController {

    private static  final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domian;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;


    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    /**
     * 上传用户头像
     * @param headerImage
     * @param model
     * @return
     */
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if (headerImage == null){
            model.addAttribute("error","您还没有选择图片");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (suffix == null){
            model.addAttribute("error","文件格式错误");
            return "/site/setting";
        }

        fileName = CommunityUtil.generateUUID() + suffix;
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败:" + e.getMessage());
            throw new RuntimeException("文件上传失败",e);
        }

        //更新用户头像
        //http://localhost:8080/community/user/header/xxx.png
        String path = domian + contextPath + "/user/header" + fileName;
        User user = hostHolder.getVal();
        userService.updateHeader(user.getId(), path);

        return "redirect:/index";
    }

    /**
     * 读取头像
     * @param fileName
     * @param response
     */
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/"+ suffix);
        fileName = uploadPath + "/" + fileName;
        try(
                OutputStream os = response.getOutputStream();
                FileInputStream is = new FileInputStream(new File(fileName));
                ) {

            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = is.read(buffer)) != -1){
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败:" + e.getMessage());
            throw new RuntimeException("读取头像失败",e);
        }
    }


}
