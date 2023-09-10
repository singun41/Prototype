package com.prototype.jwtstudy.common.security;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jasypt.encryption.pbe.PBEStringCleanablePasswordEncryptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.prototype.jwtstudy.common.config.ConfigProperties;
import com.prototype.jwtstudy.common.etc.DateTimeGenerator;
import com.prototype.jwtstudy.common.security.vo.Jwt;
import com.prototype.jwtstudy.domain.entity.Member;
import com.prototype.jwtstudy.domain.repository.JpaRepositoryMember;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
  /*
    token 방식은 기본적으로 유저 정보를 token에 담아서 프론트에 전달한 뒤에 지속적으로 사용하므로
    token에 담는 데이터를 평문으로 보내면 안 된다.
    그래서 Jasypt를 이용해서 암호화한 문자열로 token에 담아서 보내고
    token을 받으면 복호화해서 유저 및 권한을 구분하도록 한다.
  */
  private final PBEStringCleanablePasswordEncryptor jasyptStringEncryptor;
  private final String secretKey = "cXlZho4532d2lzMgTkQZGEzN6l0UJ49Ham74Gs2PcjGs8nUt";   // 최소 43개의 문자열
  private final Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
  private final String strAuths = "auths";
  private final String bearer = "Bearer";
  private final String prefixBearer = "Bearer-";
  private final JpaRepositoryMember jpaRepositoryMember;


  Jwt generate(Authentication authentication) {   // 로그인 시 생성
    log.info("generate() called.");
    String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

    // user id를 인코딩해서 보낸다.
    String encodedId = jasyptStringEncryptor.encrypt(authentication.getName());
    String encodedAuths = jasyptStringEncryptor.encrypt(authorities);

    String accessToken = generateAccessToken(encodedId, encodedAuths);
    String refreshToken = generateRefreshToken(encodedId);

    return Jwt.builder()
    .grantType(bearer)
    .accessToken(accessToken)
    .refreshToken(refreshToken).build();
  }


  String getNewAccessToken(String refreshToken) {   // 로그인 이후 AccessToken 만료시 RefreshToken으로 재생성
    log.info("getNewAccessToken() called.");
    if(validation(refreshToken)) {
      Claims claims = getClaims(refreshToken);
      String encodedId = claims.getSubject();
      String userId = jasyptStringEncryptor.decrypt(encodedId);

      // 유저 권한이 변경되었을 수 있기 때문에 DB에서 다시 조회.
      Optional<Member> member = jpaRepositoryMember.findByUserId(userId);
      String authorities = member.get().getRoles().stream().map(s -> s.name()).collect(Collectors.joining(","));
      String encodedAuths = jasyptStringEncryptor.encrypt(authorities);

      String newAccessToken = generateAccessToken(encodedId, encodedAuths);
      return newAccessToken;
    
    } else {
      return null;
    }
  }


  String getNewRefreshToken(String refreshToken) {   // RefreshToken는 만료전에 갱신해야 함.
    log.info("getNewRefreshToken() called.");
    if(validation(refreshToken)) {
      Claims claims = getClaims(refreshToken);
      String encodedId = claims.getSubject();
      String newRefreshToken = generateRefreshToken(encodedId);
      return newRefreshToken;
    
    } else {
      return null;
    }
  }


  String getTokenExpirationDatetime(String token) {
    Claims claims = getClaims(token);
    if(claims != null) {
      Date expDt = claims.getExpiration();
      return LocalDateTime.ofInstant(expDt.toInstant(), ConfigProperties.ZONE_ID).format(ConfigProperties.FORMATTER_DATETIME);
    
    } else {
      return null;
    }
  }


  private String generateAccessToken(String encodedId, String encodedAuths) {
    LocalDateTime expDt = DateTimeGenerator.now().plusMinutes(1);
    log.info("access token expired: {}", DateTimeGenerator.toString(expDt));

    Date accessTokenExpDt = Date.from(expDt.atZone(ConfigProperties.ZONE_ID).toInstant());

    return prefixBearer + Jwts.builder().setSubject(encodedId).claim(strAuths, encodedAuths)
    .setExpiration(accessTokenExpDt).signWith(key, SignatureAlgorithm.HS256).compact();
  }


  private String generateRefreshToken(String encodedId) {
    LocalDateTime expDt = DateTimeGenerator.now().plusMinutes(10);
    log.info("refresh token expired: {}", expDt);

    Date refreshTokenExpDt = Date.from(expDt.atZone(ConfigProperties.ZONE_ID).toInstant());

    return Jwts.builder().setSubject(encodedId)
    .setExpiration(refreshTokenExpDt).signWith(key, SignatureAlgorithm.HS256).compact();
  }


  Authentication getAuthentication(String accessToken) {
    log.info("getAuthentication() called.");
    Claims claims = getClaims(accessToken);

    if(claims == null)
      throw new RuntimeException("Claim has no token.");
    if(claims.get(strAuths) == null)
      throw new RuntimeException("Claim has no authorities.");

    // 클레임에서 정보 가져오기
    // 인코딩된 유저 ID를 디코딩.
    String userIdEnc = claims.getSubject();
    String userId = jasyptStringEncryptor.decrypt(userIdEnc);
    log.info("User Id --> {}", userId);

    // 인코딩된 유저 권한을 디코딩.
    String encodedAuths = claims.get(strAuths).toString();
    String authorities = jasyptStringEncryptor.decrypt(encodedAuths);
    log.info("Auths --> {}", authorities);

    Collection<? extends GrantedAuthority> grantedAuthorities =
    Arrays.stream(authorities.split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

    // DB를 조회하지 않고 token으로 유저를 구분할 수 있다. UserDetails 객체를 만들어서 Authentication 리턴
    UserDetails principal = new User(userId, "", grantedAuthorities);
    return new UsernamePasswordAuthenticationToken(principal, "", grantedAuthorities);
  }


  private Claims getClaims(String token) {
    try {
      log.info("getClaim() called.");
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token.replace(prefixBearer, "")).getBody();
    
    } catch(ExpiredJwtException e) {
      log.error("getClaim() ExpiredJwtException. {}", e.getMessage());
      return e.getClaims();

    } catch(UnsupportedJwtException e) {
      log.error("getClaim() UnsupportedJwtException. {}", e.getMessage());
    
    } catch(MalformedJwtException | SecurityException e) {
      log.error("getClaim() MalformedJwtException. {}", e.getMessage());
    
    } catch(IllegalArgumentException e) {
      log.error("getClaim() IllegalArgumentException. {}", e.getMessage());
    
    } catch(Exception e) {
      log.error("getClaim() Exception. {}", e.getMessage());
    }
    return null;
  }


  boolean validation(String token) {   // JwtAuthenticationFilter에서 호출.
    try {
      log.info("validation() called.");
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token.replace(prefixBearer, ""));
      return true;

    } catch(ExpiredJwtException e) {
      log.error("validation() ExpiredJwtException. {}", e.getMessage());

    } catch(UnsupportedJwtException e) {
      log.error("validation() UnsupportedJwtException. {}", e.getMessage());
    
    } catch(MalformedJwtException | SecurityException e) {
      log.error("validation() MalformedJwtException. {}", e.getMessage());
    
    } catch(IllegalArgumentException e) {
      log.error("validation() IllegalArgumentException. {}", e.getMessage());
    
    } catch(Exception e) {
      log.error("validation() Exception. {}", e.getMessage());
    }
    return false;
  }
}
