package com.chenlf.community.mapper.elasticsearch;

import com.chenlf.community.entity.DiscussPost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
    List<DiscussPost> findByContentLike(String content);
    Page<DiscussPost> findByContentContains(String content, Pageable page);
}