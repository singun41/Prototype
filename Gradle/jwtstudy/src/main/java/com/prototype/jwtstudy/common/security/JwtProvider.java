package com.prototype.jwtstudy.common.security;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
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
import com.prototype.jwtstudy.common.security.vo.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
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
    token에 담기는 정보 자체를 평문으로 보내면 위험할 수 있다.
    그래서 Jasypt 라이브러리를 이용해서 암호화한 문자열로 token에 담아서 보내고
    token을 받으면 다시 복호화해서 유저 및 권한을 구분하도록 한다.
  */
  private final PBEStringCleanablePasswordEncryptor jasyptStringEncryptor;
  private final String secretKey = "cXlZho4532d2lzMgTkQZGEzN6l0UJ49Ham74Gs2PcjGs8nUt";   // 최소 43개의 문자열
  private final Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
  private final String auths = "auths";
  private final String bearer = "Bearer";


  public Jwt generate(Authentication authentication) {
    String authorities =
    authentication.getAuthorities().stream().map(
      GrantedAuthority::getAuthority
    ).collect(Collectors.joining(","));

    Date accessTokenExpDt =
    Date.from(LocalDateTime.now().plusMinutes(5).atZone(ConfigProperties.ZONE_ID).toInstant());

    Date refreshTokenExpDt =
    Date.from(LocalDateTime.now().plusMinutes(30).atZone(ConfigProperties.ZONE_ID).toInstant());

    // user id를 인코딩해서 보낸다.
    String encodedId = jasyptStringEncryptor.encrypt(authentication.getName());
    String encodedAuths = jasyptStringEncryptor.encrypt(authorities);

    String accessToken =
    Jwts.builder()
    .setSubject(encodedId)
    .claim(auths, encodedAuths)
    .setExpiration(accessTokenExpDt)
    .signWith(key, SignatureAlgorithm.HS256).compact();

    String refreshToken =
    Jwts.builder()
    .setExpiration(refreshTokenExpDt)
    .signWith(key, SignatureAlgorithm.HS256).compact();

    return Jwt.builder().grantType(bearer).accessToken(accessToken).refreshToken(refreshToken).build();
  }


  public Authentication getAuthentication(String accessToken) {
    Claims claims = getClaims(accessToken);
    if(claims.get(auths) == null)
      throw new RuntimeException("권한 정보가 없는 토큰입니다.");

    // 클레임에서 정보 가져오기
    // 인코딩된 유저 ID를 디코딩.
    String userIdEnc = claims.getSubject();
    String userId = jasyptStringEncryptor.decrypt(userIdEnc);
    log.info("{} --> {}", userIdEnc, userId);

    // 인코딩된 유저 권한을 디코딩.
    String strAuthsEnc = claims.get(auths).toString();
    String strAuths = jasyptStringEncryptor.decrypt(strAuthsEnc);
    log.info("{} --> {}", strAuthsEnc, strAuths);

    Collection<? extends GrantedAuthority> authorities =
    Arrays.stream(strAuths.split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

    // DB를 조회하지 않고 JWT로 유저를 구분할 수 있다. UserDetails 객체를 만들어서 Authentication 리턴
    UserDetails principal = new User(userId, "", authorities);
    return new UsernamePasswordAuthenticationToken(principal, "", authorities);
  }


  private Claims getClaims(String accessToken) {
    try {
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
    
    } catch(ExpiredJwtException e) {
      log.error("token expired.");
      return e.getClaims();

    } catch(InvalidClaimException e) {
      log.error("invalid token.");
      return e.getClaims();
    }
  }


  public boolean validation(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;

    } catch(SecurityException | MalformedJwtException e) {
      log.error("Invalid JWT.");

    } catch(ExpiredJwtException e) {
      log.error("token expired.");

    } catch(UnsupportedJwtException e) {
      log.error("Unsupported JWT.");
    
    } catch(IllegalArgumentException e) {
      log.error("JWT claims string is empty.");
    }
    return false;
  }
}
