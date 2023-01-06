package com.prototype.sessionredis.data;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import com.prototype.sessionredis.vo.Auth;

public class LoginUser extends User {
    private Member member;

    public LoginUser(Member member) {
        super(
            member.getUserId(),
            member.getPassword(),
            member.isEnabled(),
            true,
            true,
            true,
            AuthorityUtils.createAuthorityList(member.getAuths().stream().map(Auth::name).collect(Collectors.joining(",")))
        );
        this.member = member;
    }

    public Member getMember() {
        return member;
    }
}
