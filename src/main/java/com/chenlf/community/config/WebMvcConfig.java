package com.chenlf.community.config;

import com.chenlf.community.intercepter.LoginRequiredIntercepter;
import com.chenlf.community.intercepter.LoginTicketIntercepter;
import com.chenlf.community.intercepter.MessageIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 
 * @author ChenLF
 * @date 2022/09/01 23:02
 **/

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginTicketIntercepter loginTicketIntercepter;

//    @Autowired
//    private LoginRequiredIntercepter loginRequiredIntercepter;

    @Autowired
    private MessageIntercepter messageIntercepter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketIntercepter)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.png", "/**/*.jpeg");
//        registry.addInterceptor(loginRequiredIntercepter)
//                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.png", "/**/*.jpeg");
        registry.addInterceptor(messageIntercepter)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.png", "/**/*.jpeg");
    }
}
