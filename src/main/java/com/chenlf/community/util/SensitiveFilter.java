package com.chenlf.community.util;

import lombok.Data;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词过滤器
 * @author ChenLF
 * @date 2022/09/18 23:25
 **/

@Component
@Data
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    private static final String REPLASEMENT = "***";
    private TrieNode rootNode = new TrieNode();

    //定义敏感词前缀树
    public class TrieNode{
        private boolean isKeyWordEnd = false;
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c,node);
        }

        public TrieNode getSubNode(Character character){
            return subNodes.get(character);
        }

        @Override
        public String toString() {
            return "TrieNode{" +
                    "isKeyWordEnd=" + isKeyWordEnd +
                    ", subNodes=" + subNodes +
                    '}';
        }
    }
    //根据敏感词初始化前缀树
    @PostConstruct
    public void init(){
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            ) {
            String tempStr;
            while((tempStr = reader.readLine())!=null){
                this.addKeyWord(tempStr);
            }
        }catch (IOException e){
            logger.debug("敏感词文本加载失败: "+ e.getMessage());
        }
    }

    /**
     * 添加字符进前缀树
     * @param tempStr
     */
    private void addKeyWord(String tempStr){
        TrieNode tempNode = rootNode;
        for (int i = 0; i < tempStr.length(); i++) {
            char r = tempStr.charAt(i);
            TrieNode subNode = tempNode.getSubNode(r);
            if (subNode == null){
                subNode = new TrieNode();
                tempNode.addSubNode(r,subNode);
            }
            tempNode = subNode;
            if (i == tempStr.length() - 1){
                tempNode.setKeyWordEnd(true);
                tempNode = rootNode;
            }
        }
    }

    //编写敏感词过滤方法

    /**
     * 敏感词过滤方法
     * @param text 待过滤文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }
        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;
        StringBuilder sb = new StringBuilder();
        while(begin < text.length()){
            char  c = text.charAt(position);
            if (this.isSymbol(c)){
                if (tempNode == rootNode){
                    sb.append(c);
                    begin = ++position;
                }else {
                    position++;
                }
                continue;
            }

            //检查下级节点
            TrieNode subNode = tempNode.getSubNode(c);
            //匹配不上敏感词
            if (subNode == null){
                sb.append(c);
                position = ++begin;
                tempNode = rootNode;
            //敏感词前缀数叶子节点
            }else if (subNode.isKeyWordEnd()){
                sb.append(REPLASEMENT);
                begin = ++position;
                tempNode = rootNode;
            //敏感词匹配上,只是疑似,需要匹配到叶子节点
            }else{
                position ++;
                tempNode = subNode;
                //疑似敏感词,但是遍历到文本最后的字符不是叶子节点 例如:abc为敏感词 检测文本为fab
                if (position >= text.length()){
                    sb.append(text.charAt(begin));
                    begin = position;
                }
            }
        }

        return sb.toString();
    }

    /**
     * 是否是特殊符号 0x2E80 ~ 0x9FFF是东亚文字范围
     * @param c
     * @return
     */
    private boolean isSymbol(char c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }
}
