package com.studycloud.gateway.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studycloud.gateway.entity.Member;

public interface JpaRepositoryMember extends JpaRepository<Member, UUID> {
  Optional<Member> findByUserId(String id);
}
