package com.chenlf.community.entity;

import lombok.Data;

/**
 * 封装分页信息
 * @author ChenLF
 * @date 2022/07/13 23:44
 **/

@Data
public class Page {

    private int current = 1;

    private int limit = 10;

    //数据总量
    private int rows;

    //查询路径
    private String path;

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public void setLimit(int limit) {
        if (limit >=1 && limit<=100){
            this.limit = limit;
        }
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    /**
     * 获取当前页的起始行
     * @return
     */
    public int getOffset(){
        return (current - 1) * limit;
    }

    /**
     * 获取页数
     * @return
     */
    public int getTotal(){
        if (rows % limit == 0){
            return rows / limit;
        }else{
            return rows / limit + 1;
        }
    }

    /**
     * 获取起始页
     * @return
     */
    public int getFrom(){
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取终止页
     * @return
     */
    public int getTo(){
        int to = current + 2;
        int total = this.getTotal();
        return to > total ? total : to;
    }

}
