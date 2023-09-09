package com.prototype.jwtstudy.common.security;

import java.security.Key;
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
import com.prototype.jwtstudy.common.etc.DateTimeGenerator;
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
    token에 담는 데이터를 평문으로 보내면 안 된다.
    그래서 Jasypt를 이용해서 암호화한 문자열로 token에 담아서 보내고
    token을 받으면 복호화해서 유저 및 권한을 구분하도록 한다.
  */
  private final PBEStringCleanablePasswordEncryptor jasyptStringEncryptor;
  private final String secretKey = "cXlZho4532d2lzMgTkQZGEzN6l0UJ49Ham74Gs2PcjGs8nUt";   // 최소 43개의 문자열
  private final Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
  private final String strAuths = "auths";
  private final String bearer = "Bearer";
  private final ConcurrentHashMap<String, UserInfo> cacheUserInfo = new ConcurrentHashMap<>(1000);


  Jwt generate(Authentication authentication) {   // 로그인 시 생성
    log.info("generate() called.");
    String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

    // user id를 인코딩해서 보낸다.
    String encodedId = jasyptStringEncryptor.encrypt(authentication.getName());
    String encodedAuths = jasyptStringEncryptor.encrypt(authorities);

    String accessToken = generateAccessToken(encodedId, encodedAuths);
    String refreshToken = generateRefreshToken();

    caching(refreshToken, new UserInfo(encodedId, encodedAuths));

    return Jwt.builder()
    .grantType(bearer)
    .accessToken(bearer + "-" + accessToken)
    .refreshToken(refreshToken).build();
  }


  Jwt regenerate(String refreshToken) {   // 로그인 이후 만료시 RefreshToken으로 재생성
    UserInfo userInfo = cacheUserInfo.get(refreshToken);

    if(userInfo == null) {
      log.warn("Cannot find the userInfo using the refreshToken.");
      return null;
    }

    log.info("regenerate() called.");
    String newAccessToken = generateAccessToken(userInfo.getEncodedId(), userInfo.getEncodedAuths());
    String newRefreshToken = generateRefreshToken();

    // AccessToken을 새로 발급할 때 RefreshToken도 새로 발급해서 보안을 강화한다.
    // 이 조건으로 유저는 현재 1개의 토큰만 로그인이 가능하다.
    // 여러 기기에서 로그인할 수 있게 하려면 유저별로 AccessToken과 RefreshToken을 DB에 저장하도록 변경하면 된다.
    removeCache(refreshToken);
    caching(newRefreshToken, userInfo);

    // AccessToken을 재생성할 때 RefreshToken도 같이 교체해줌으로써 계속 활동하는 유저는 로그인 상태가 연장된다.
    // RefreshToken이 만료될 때까지 활동이 없는 유저는 다시 로그인해야 한다.
    return Jwt.builder()
    .grantType(bearer)
    .accessToken(bearer + "-" + newAccessToken)
    .refreshToken(newRefreshToken).build();
  }


  private String generateAccessToken(String encodedId, String encodedAuths) {
    Date accessTokenExpDt = Date.from(DateTimeGenerator.now().plusMinutes(5).atZone(ConfigProperties.ZONE_ID).toInstant());
    log.info("access token expired: {}", accessTokenExpDt);

    return Jwts.builder().setSubject(encodedId).claim(strAuths, encodedAuths)
    .setExpiration(accessTokenExpDt).signWith(key, SignatureAlgorithm.HS256).compact();
  }


  private String generateRefreshToken() {
    Date refreshTokenExpDt = Date.from(DateTimeGenerator.now().plusMinutes(20).atZone(ConfigProperties.ZONE_ID).toInstant());
    log.info("refresh token expired: {}", refreshTokenExpDt);

    return Jwts.builder().setExpiration(refreshTokenExpDt).signWith(key, SignatureAlgorithm.HS256).compact();
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
    String strAuthsEnc = claims.get(strAuths).toString();
    String strAuths = jasyptStringEncryptor.decrypt(strAuthsEnc);
    log.info("Auths --> {}", strAuths);

    Collection<? extends GrantedAuthority> authorities =
    Arrays.stream(strAuths.split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

    // DB를 조회하지 않고 token으로 유저를 구분할 수 있다. UserDetails 객체를 만들어서 Authentication 리턴
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


  boolean validation(String token) {   // JwtAuthenticationFilter에서 호출.
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


  private void caching(String refreshToken, UserInfo userInfo) {
    log.info("userInfo cached.");
    cacheUserInfo.put(refreshToken, userInfo);
  }


  void removeCache(String refreshToken) {
    cacheUserInfo.remove(refreshToken);
    log.info("userInfo removed.");
  }
}
