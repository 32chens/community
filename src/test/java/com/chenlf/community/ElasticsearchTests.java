package com.chenlf.community;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.chenlf.community.entity.DiscussPost;
import com.chenlf.community.mapper.DiscussPostMapper;
import com.chenlf.community.mapper.elasticsearch.DiscussPostRepository;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author ChenLF
 * @date 2022/10/22 17:12
 **/

@SpringBootTest(classes = {CommunityApplication.class})
@ContextConfiguration(classes = ElasticsearchTests.class)
public class ElasticsearchTests {


    @Resource
    private DiscussPostMapper discussPostMapper;

    @Resource
    private DiscussPostRepository discussPostRepository;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    public void testInsert() {
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
        DiscussPost post = discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));
        System.out.println(post);
    }

    @Test
    public void testInsertList() {
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134, 0, 100));
    }

    @Test
    public void testUpdate() {
        DiscussPost post = discussPostMapper.selectDiscussPostById(241);
        post.setContent("我是新人,使劲灌水.");
        discussPostRepository.save(post);
    }

    @Test
    public void testDelete() {
         discussPostRepository.deleteById(243);
//        discussPostRepository.deleteAll();
    }

    @Test
    public void testFindByIdAll(){
        List<Integer> list = new ArrayList<>();
        list.add(242);
        Iterable<DiscussPost> allById = discussPostRepository.findAllById(list);
        for (DiscussPost post : allById) {
            System.out.println(post);
        }
    }

    @Test
    public void testFindByUserNameLike(){
        List<DiscussPost> posts = discussPostRepository.findByContentLike("新人");
        System.out.println("posts:"+posts);

        Page<DiscussPost> page = discussPostRepository.findByContentContains("爱国", PageRequest.of(0, 100));
        System.out.println(page.getContent());
    }



    @Test
    public void testSearchByRepository() {
        // withQuery()用于构造搜索条件
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

        long count = elasticsearchRestTemplate.count(searchQuery, DiscussPost.class);
        System.out.println("共查询到: "+ count +"条数据");
        System.out.println("2共查询到: "+ search.getTotalHits() +"条数据");

        //得到查询返回的内容
        List<SearchHit<DiscussPost>> searchHits = search.getSearchHits();
        // 设置一个最后需要返回的实体类集合
        List<DiscussPost> discussPosts = new ArrayList<>();
        // 遍历返回的内容，进行处理
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
        for (DiscussPost post : discussPosts) {
            System.out.println(post);
        }


    }



}
