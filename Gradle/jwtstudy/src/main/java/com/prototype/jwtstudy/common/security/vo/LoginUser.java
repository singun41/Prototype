package com.prototype.jwtstudy.common.security.vo;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import com.prototype.jwtstudy.domain.entity.Member;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginUser extends User {
  private static final long serialVersionUID = 1L;
    private final Member member;


    public LoginUser(Member member) {
      super(
        member.getUserId(),
        member.getPassword(),
        member.isEnabled(),
        true,
        true,
        true,
        AuthorityUtils.createAuthorityList(
          // 유저 권한을 Set<> 컬렉션으로 처리하다보니 toString()으로 문자열 전환하면 괄호 및 콤마, 공백이 생성된다. 콤마는 제외하고 나머지를 제거해줘야 한다.
          member.getRoles().toString().replace("[", "").replace("]", "").replace(" ", "")
        )
      );

      this.member = member;
      log.info("{}", member);
    }


    public Member getMember() {
      return this.member;
    }
}
