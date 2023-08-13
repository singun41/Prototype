package com.prototype.jwtstudy.common.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.prototype.jwtstudy.common.security.vo.LoginUser;
import com.prototype.jwtstudy.domain.entity.Member;
import com.prototype.jwtstudy.domain.repository.JpaRepositoryMember;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final JpaRepositoryMember jpaRepositoryMember;


  @Override
  public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
    log.info("userId=[{}]", userId);

    Optional<Member> member = jpaRepositoryMember.findByUserId(userId);
    if(member.isPresent()){
      log.info("User founded. {}", member.get());
      return new LoginUser(member.get());
    
    } else {
      throw new UsernameNotFoundException(userId);
    }
  }
}
