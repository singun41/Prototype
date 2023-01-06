package com.prototype.sessionredis.vo;

public enum Role {
    ADMIN("시스템 관리자"), MANAGER("관리자"), USER("사용자");

    private String title;
    private Role(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }
}
