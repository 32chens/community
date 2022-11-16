package com.chenlf.community.controller;

import com.chenlf.community.entity.DiscussPost;
import com.chenlf.community.entity.Page;
import com.chenlf.community.service.DiscussPostService;
import com.chenlf.community.service.ElasticSearchService;
import com.chenlf.community.service.LikeService;
import com.chenlf.community.service.UserService;
import com.chenlf.community.util.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author ChenLF
 * @date 2022/10/29 16:33
 **/

@Controller
public class SearchController {

    @Autowired
    private ElasticSearchService searchService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model){
        List<DiscussPost> posts = searchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        List<Map<String,Object>> postVOs = new ArrayList<>();
        for (DiscussPost post : posts) {
            Map<String,Object> map = new HashMap<>();
            map.put("post",post);
            map.put("user",userService.findUserById(post.getUserId()));
            map.put("likeCount",likeService.getLikeCount(SystemConstants.ENTITY_TYPE_POST, post.getId()));
            postVOs.add(map);
        }
        model.addAttribute("discussPosts", postVOs);
        model.addAttribute("keyword", keyword);

        page.setPath("/search?keyword="+keyword);
        page.setRows(posts==null ? 0 : posts.size());
        return "/site/search";
    }

}
