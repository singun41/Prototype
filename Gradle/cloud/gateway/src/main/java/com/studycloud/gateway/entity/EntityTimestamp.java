package com.studycloud.gateway.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.ToString;

@MappedSuperclass
@ToString
public abstract class EntityTimestamp {
  @Column(name = "c_create_date", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createDate;

  @Column(name = "c_update_date", nullable = false)
  @UpdateTimestamp
  private LocalDateTime updateDate;
}
