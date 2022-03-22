package com.lmdd.utils;

import com.lmdd.config.AppConfig;
import com.lmdd.pojo.login.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Data;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * @author LM_MAX
 * @date 2022/3/17
 */
@Component
@Data
public class JwtUtil {

    @Autowired
    private final AppConfig appConfig;

    // 用于签名
    public static final Key accesskey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public static final Key refreshKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public String creatJwtToken(UserDetails userDetails, Key key, long timeToExpire){
        User user = (User) userDetails;
        val now = System.currentTimeMillis();
        return Jwts.builder()
                .setId("login_demo")
                .claim("authorities", userDetails.getAuthorities().stream()
                        .map( authority -> authority.getAuthority())
                        .collect(toList()))
                .claim("nickname",user.getNickname())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + timeToExpire))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String creatAccessToken(UserDetails userDetails){
        return creatJwtToken(userDetails, accesskey, appConfig.getJwt().getAccessTokenExpireTime());
    }

    public String createAccessTokenWithRefreshToken(String token){
        return parseClaims(token, refreshKey)
                .map(claims -> Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + appConfig.getJwt().getAccessTokenExpireTime()))
                    .setIssuedAt(new Date())
                    .signWith(accesskey, SignatureAlgorithm.HS512)
                    .compact()
                )
                .orElseThrow(() -> new AccessDeniedException("访问被拒绝"));
    }

    private Optional<Claims> parseClaims(String token, Key key){
        try{
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return Optional.of(claims);
        }catch (Exception e){
            return Optional.empty();
        }
    }

    public String createRefreshToken(UserDetails userDetails){
        return creatJwtToken(userDetails, refreshKey, appConfig.getJwt().getRefreshTokenExpireTime());
    }

//    public boolean validateToken(String jwtToken, Key signKey, boolean isExpireInvalidate) {
//        try {
//            Jwts.parserBuilder().setSigningKey(signKey).build().parseClaimsJws(jwtToken);
//            return true;
//        } catch (ExpiredJwtException | SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
//            return false;
//        }
//    }

    public boolean validateToken(String jwtToken, Key signKey, boolean isExpireInvalidate) {
        try {
            Jwts.parserBuilder().setSigningKey(signKey).build().parseClaimsJws(jwtToken);
            return true;
        } catch (ExpiredJwtException | SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            if (e instanceof ExpiredJwtException) {
                return !isExpireInvalidate;
            }
        }
        return false;
    }

    public boolean validateAccessToken(String jwtToken) {
        return validateToken(jwtToken, accesskey, true);
    }

    public boolean validateRefreshToken(String jwtToken) {
        return validateToken(jwtToken, refreshKey, true);
    }

    public boolean validateAccessTokenWithoutExpiration(String jwtToken) {
        return validateToken(jwtToken, accesskey, false);
    }

    public boolean validateRefreshTokenWithoutExpiration(String jwtToken) {
        return validateToken(jwtToken, refreshKey, false);
    }
}
