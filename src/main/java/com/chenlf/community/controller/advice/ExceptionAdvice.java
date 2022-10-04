package com.chenlf.community.controller.advice;

import com.chenlf.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * 
 * @author ChenLF
 * @date 2022/10/04 15:14
 **/

@ControllerAdvice(annotations = Controller.class) //只处理Controller注解的bean
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler
    public void handlerException(Exception e , HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器异常: "+ e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }
        String xRequestedWith = request.getHeader("x-requested-with");
        //判断 是 同步异步
        if ("XMLHttpRequest".equals(xRequestedWith)){
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(500,"服务器异常"));
        }else{
            response.sendRedirect(request.getContextPath() + "/bad");
        }

    }
}
