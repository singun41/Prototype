package com.prototype.sessionredis.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.prototype.sessionredis.data.LoginUser;
import com.prototype.sessionredis.data.Member;
import com.prototype.sessionredis.service.MemberService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberService service;

    @Autowired
    public UserDetailsServiceImpl(MemberService service) {
        this.service = service;
        log.info("UserDetailsService " + this.getClass().getName() + " has been created.");
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Member member = service.getMember(id);
        
        if(member.getId() == null)
            throw new UsernameNotFoundException(id);
        else
            return new LoginUser(member);
    }
}
