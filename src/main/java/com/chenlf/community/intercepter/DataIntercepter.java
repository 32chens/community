package com.chenlf.community.intercepter;

import com.chenlf.community.entity.User;
import com.chenlf.community.service.DataService;
import com.chenlf.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author ChenLF
 * @date 2022/11/12 20:46
 **/

@Component
public class DataIntercepter implements HandlerInterceptor {

    @Autowired
    private DataService datasService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计UV
        String ip = request.getRemoteHost();
        datasService.recordUV(ip);
        //统计DAU
        User user = hostHolder.getVal();
        if(user != null){
            datasService.recordDAU(user.getId());
        }
        return true;
    }
}
