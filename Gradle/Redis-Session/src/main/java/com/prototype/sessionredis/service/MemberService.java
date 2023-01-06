package com.prototype.sessionredis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.prototype.sessionredis.data.Member;
import com.prototype.sessionredis.entity.EntityMember;
import com.prototype.sessionredis.helper.MemberConverter;
import com.prototype.sessionredis.repository.MemberRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class MemberService {
    private final MemberRepository repo;
    private final MemberConverter converter;

    @Autowired
    public MemberService(MemberRepository repo, MemberConverter converter) {
        this.repo = repo;
        this.converter = converter;
        log.info("Service " + this.getClass().getName() + " has been created.");
    }

    public Member getMember(String id) {
        return converter.convertMember( repo.findByUserId(id).orElseGet(EntityMember::new) );
    }
}
