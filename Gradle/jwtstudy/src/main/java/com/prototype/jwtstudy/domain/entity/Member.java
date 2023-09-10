package com.prototype.jwtstudy.domain.entity;

import java.util.Set;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.prototype.jwtstudy.common.config.ConfigProperties;
import com.prototype.jwtstudy.common.entity.EntityTimestamp;
import com.prototype.jwtstudy.domain.vo.MemberRole;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "tbl_member", indexes = @Index(columnList = "c_user_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString(callSuper = false, exclude = {"uuid", "password"})
public class Member extends EntityTimestamp {
  @Id
  @Column(name = "c_uuid", columnDefinition = ConfigProperties.COLUMN_DEFINITION_UUID, nullable = false, updatable = false)
  @GeneratedValue(generator = ConfigProperties.UUID_CUSTOM_GENERATOR, strategy = GenerationType.IDENTITY)
  @GenericGenerator(name = ConfigProperties.UUID_CUSTOM_GENERATOR, strategy = ConfigProperties.UUID_CUSTOM_GENERATOR_PACKAGE)
  private UUID uuid;

  @Column(name = "c_user_id", length = 50, nullable = false, updatable = false, unique = true)
  private String userId;

  @Column(name = "c_password", length = 50, nullable = false, updatable = false, unique = true)
  private String password;

  @Column(name = "c_enabled", nullable = false, updatable = true)
  private boolean enabled;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tbl_member_role", joinColumns = @JoinColumn(name = "c_member_uuid"))
  @Column(name = "c_value")
  @Enumerated(EnumType.STRING)
  private Set<MemberRole> roles;
}
