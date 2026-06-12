package com.example.accounts.db;

/**
 * 搜索历史记录的数据模型类
 */
public class SearchHistoryBean {
    private int id;
    private String keyword;
    private long searchTime;
    private int searchCount;

    public SearchHistoryBean() {
    }

    public SearchHistoryBean(int id, String keyword, long searchTime, int searchCount) {
        this.id = id;
        this.keyword = keyword;
        this.searchTime = searchTime;
        this.searchCount = searchCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public long getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(long searchTime) {
        this.searchTime = searchTime;
    }

    public int getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(int searchCount) {
        this.searchCount = searchCount;
    }
}
