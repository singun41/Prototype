package com.studycloud.gateway.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.studycloud.gateway.entity.Member;
import com.studycloud.gateway.repository.JpaRepositoryMember;
import com.studycloud.gateway.security.vo.LoginUser;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {
  private final JpaRepositoryMember jpaRepositoryMember;

  @Override
  public Mono<UserDetails> findByUsername(String username) {
    Optional<Member> optMember = jpaRepositoryMember.findByUserId(username);
    if(optMember.isPresent()) {
      return Mono.just(new LoginUser(optMember.get()));
    
    } else {
      return Mono.empty();
    }
  }
}
