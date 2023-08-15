package com.prototype.jwtstudy.common.security;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
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
import com.prototype.jwtstudy.common.security.vo.UserInfo;

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
    token에 담기는 정보 자체를 평문으로 보내면 위험할 수 있다.
    그래서 Jasypt 라이브러리를 이용해서 암호화한 문자열로 token에 담아서 보내고
    token을 받으면 다시 복호화해서 유저 및 권한을 구분하도록 한다.
  */
  private final PBEStringCleanablePasswordEncryptor jasyptStringEncryptor;
  private final String secretKey = "cXlZho4532d2lzMgTkQZGEzN6l0UJ49Ham74Gs2PcjGs8nUt";   // 최소 43개의 문자열
  private final Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
  private final String strAuths = "auths";
  private final String bearer = "Bearer";
  private final ConcurrentHashMap<String, UserInfo> userInfoCache = new ConcurrentHashMap<>(1000);


  public Jwt generate(Authentication authentication) {
    String authorities =
    authentication.getAuthorities().stream().map(
      GrantedAuthority::getAuthority
    ).collect(Collectors.joining(","));

    Date accessTokenExpDt =
    Date.from(LocalDateTime.now().plusSeconds(45).atZone(ConfigProperties.ZONE_ID).toInstant());

    Date refreshTokenExpDt =
    Date.from(LocalDateTime.now().plusMinutes(10).atZone(ConfigProperties.ZONE_ID).toInstant());

    log.info("accessTokenExpDt: {}, refreshTokenExpDt: {}", accessTokenExpDt, refreshTokenExpDt);

    // user id를 인코딩해서 보낸다.
    String encodedId = jasyptStringEncryptor.encrypt(authentication.getName());
    String encodedAuths = jasyptStringEncryptor.encrypt(authorities);

    String accessToken =
    Jwts.builder()
    .setSubject(encodedId)
    .claim(strAuths, encodedAuths)
    .setExpiration(accessTokenExpDt)
    .signWith(key, SignatureAlgorithm.HS256).compact();

    String refreshToken =
    Jwts.builder()
    .setExpiration(refreshTokenExpDt)
    .signWith(key, SignatureAlgorithm.HS256).compact();

    caching(refreshToken, new UserInfo(encodedId, encodedAuths));
    return Jwt.builder().grantType(bearer).accessToken(accessToken).refreshToken(refreshToken).build();
  }


  public Authentication getAuthentication(String accessToken) {
    log.info("getAuthentication() called.");
    Claims claims = getClaims(accessToken);

    if(claims == null)
      throw new RuntimeException("토큰이 없습니다.");

    if(claims.get(strAuths) == null)
      throw new RuntimeException("권한 정보가 없는 토큰입니다.");

    // 클레임에서 정보 가져오기
    // 인코딩된 유저 ID를 디코딩.
    String userIdEnc = claims.getSubject();
    String userId = jasyptStringEncryptor.decrypt(userIdEnc);
    log.info("{} --> {}", userIdEnc, userId);

    // 인코딩된 유저 권한을 디코딩.
    String strAuthsEnc = claims.get(strAuths).toString();
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
      log.info("getClaim() called.");
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
    
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


  public boolean validation(String token) {   // JwtAuthenticationFilter에서 호출.
    try {
      log.info("validation() called.");
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
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


  public Jwt tokenRegenerate(String refreshToken) {
    UserInfo userInfo = userInfoCache.get(refreshToken);

    if(userInfo == null) {
      log.warn("Cannot find the userInfo using the refreshToken.");
      return null;
    }

    log.info("tokenRegenerate() called.");
    Date accessTokenExpDt =
    Date.from(LocalDateTime.now().plusSeconds(45).atZone(ConfigProperties.ZONE_ID).toInstant());

    String newAccessToken =
    Jwts.builder()
    .setSubject(userInfo.getEncodedId())
    .claim(strAuths, userInfo.getEncodedAuths())
    .setExpiration(accessTokenExpDt)
    .signWith(key, SignatureAlgorithm.HS256).compact();

    return Jwt.builder().grantType(bearer).accessToken(newAccessToken).refreshToken(refreshToken).build();
  }


  private void caching(String refreshToken, UserInfo userInfo) {
    log.info("userInfo cached.");
    userInfoCache.put(refreshToken, userInfo);
  }


  void removeCache(String refreshToken) {
    userInfoCache.remove(refreshToken);
    log.info("userInfo removed.");
  }
}
