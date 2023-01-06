package com.prototype.sessionredis.helper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.prototype.sessionredis.data.Member;
import com.prototype.sessionredis.entity.EntityMember;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    // entity --> data
    @Mapping(target = "role", defaultValue = "USER")
    @Mapping(target = "auths", ignore = true)
    Member convertMember(EntityMember entity);


    // data --> entity
}
