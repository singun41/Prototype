package com.prototype.sessionredis.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prototype.sessionredis.entity.EntityMember;

@Repository
public interface MemberRepository extends JpaRepository<EntityMember, Long> {
    Optional<EntityMember> findByUserId(String userId);
}
