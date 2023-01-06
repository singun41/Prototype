package com.prototype.sessionredis.helper;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.prototype.sessionredis.data.Member;
import com.prototype.sessionredis.entity.EntityMember;
import com.prototype.sessionredis.vo.Auth;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MemberConverter {
    private final MemberMapper mapper;

    @Autowired
    public MemberConverter(MemberMapper mapper) {
        this.mapper = mapper;
        log.info("Component " + this.getClass().getName() + " has been created.");
    }


    public Member convertMember(EntityMember entity) {
        Set<Auth> auths = null;
        
        if(entity.getAuths() == null || entity.getAuths().isBlank())
            auths = Collections.emptySet();
        else
            auths = Arrays.stream(entity.getAuths().split(",")).map(Auth::valueOf).collect(Collectors.toSet());

        return mapper.convertMember(entity).setAuths( auths );
    }
}
