package com.prototype.sessionredis.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString(callSuper = true, exclude = "password")
@NoArgsConstructor(access = AccessLevel.PUBLIC)   // entity의 기본 생성자는 반드시 public or protected 이어야 한다.
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "member", indexes = @Index(columnList = "user_id"))
public class EntityMember {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "user_id", nullable = false, updatable = false, length = 30, unique = true)
    private String userId;   // Repository에서 findById 메서드를 사용할 때 헷갈릴 수 있어 userId로 작성.

    @Column(name = "password", nullable = false, updatable = true, length = 70)
    private String password;

    @Column(name = "role", nullable = false, updatable = true, length = 10)
    private String role;

    @Column(name = "auths", nullable = false, updatable = true, length = 100)
    private String auths;

    @Column(name = "enabled", nullable = false, updatable = true)
    private boolean enabled;
}
