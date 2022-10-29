package com.chenlf.community.service;

import com.chenlf.community.entity.DiscussPost;
import com.chenlf.community.mapper.elasticsearch.DiscussPostRepository;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author ChenLF
 * @date 2022/10/29 16:10
 **/

@Service
public class ElasticSearchService {

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    public void saveDiscussPost(DiscussPost discussPost){
        discussPostRepository.save(discussPost);
    }

    public void deleteDiscussPost(int id){
        discussPostRepository.deleteById(id);
    }

    public List<DiscussPost> searchDiscussPost(String keyword, int current, int limit){
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC),
                        SortBuilders.fieldSort("score").order(SortOrder.DESC),
                        SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        //查询
        SearchHits<DiscussPost> search = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);
        List<SearchHit<DiscussPost>> searchHits = search.getSearchHits();

        List<DiscussPost> discussPosts = new ArrayList<>();
        for (SearchHit<DiscussPost> hit : searchHits) {
            // 获取高亮的内容
            Map<String, List<String>> highlightFields = hit.getHighlightFields();
            // 将高亮的内容添加到content中(匹配到的如果是多段，就将第一段高亮显示)
            // 没有匹配到关键字就显示原来的title和content
            hit.getContent().setTitle(highlightFields.get("title")==null ? hit.getContent().getTitle() : highlightFields.get("title").get(0));
            hit.getContent().setContent(highlightFields.get("content")==null ? hit.getContent().getContent() : highlightFields.get("content").get(0));
            // 放到实体类中
            discussPosts.add(hit.getContent());
        }
        return discussPosts;
    }

}
