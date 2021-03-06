package com.chenlf.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * 
 * @author ChenLF
 * @date 2022/07/15 21:30
 **/
 
public class CommunityUtil {

    //生成随机的字符串
    public static String generateUUID(){
       return UUID.randomUUID().toString().replaceAll("-"," ");
    }

    //MD5加密（自能加密不能解密）
    public static String md5(String key){
        if (StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
