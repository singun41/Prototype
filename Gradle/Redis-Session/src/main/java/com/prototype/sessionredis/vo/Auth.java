package com.prototype.sessionredis.vo;

public enum Auth {
    ALL("전체 권한"),
    RND("연구개발"),
    PRODUCTION("생산"),
    SALES("영업"),
    LOGISTICS("물류"),
    HR("인사");

    private String title;
    private Auth(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }
}
