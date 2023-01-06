package com.prototype.sessionredis.data;

import java.io.Serializable;
import java.util.Set;

import com.prototype.sessionredis.vo.Auth;
import com.prototype.sessionredis.vo.Role;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Member implements Serializable {   // redis에 저장되는 세션을 구분하기 위해 Serializable 인터페이스를 구현해야 한다.
    private static final long serialVersionUID = 1L;

    private Long id;
    private String userId;
    private String password;
    private Role role;
    private Set<Auth> auths;
    private boolean enabled;
}
