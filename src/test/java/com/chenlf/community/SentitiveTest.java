package com.chenlf.community;


import com.chenlf.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author ChenLF
 * @date 2022/09/19 00:04
 **/

@SpringBootTest(classes = {SensitiveFilter.class})
@ContextConfiguration(classes = SentitiveTest.class)
public class SentitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSentitiveFilter(){
        String test = "这里可以★吸★★★★★毒★,赌赌博,赌赌博博,开开票";
        String filter = sensitiveFilter.filter(test);
        System.out.println(filter);
        test = "这里搞黄";
        filter = sensitiveFilter.filter(test);
        System.out.println(filter);
    }
}
