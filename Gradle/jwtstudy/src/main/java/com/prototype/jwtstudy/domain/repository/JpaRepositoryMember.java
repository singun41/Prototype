package com.prototype.jwtstudy.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prototype.jwtstudy.domain.entity.Member;

public interface JpaRepositoryMember extends JpaRepository<Member, UUID> {
  Optional<Member> findByUserId(String id);
}
