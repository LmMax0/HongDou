package com.lmdd.security.filter;

import com.lmdd.config.AppConfig;
import com.lmdd.utils.JwtUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**

/**
 * @author LM_MAX
 * @date 2022/3/18
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private final AppConfig appConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 初步认为合法
        if(checkJwtToken(request)){
            Optional<Claims> authorites = validateToken(request)
                    .filter(claims -> claims.get("authorities") != null);
            System.out.println("DEBUG==》"+authorites);
            Claims claims = authorites.get();
            this.setUpSpringAuthentiaction(claims);
//            Optional<Claims> claims = validateToken(request); // 获取token中的验证信息
//            System.out.println(claims);
        }else{
            log.info("认为request不合法");
//            response.sendRedirect("/login");
//            UsernamePasswordAuthenticationToken emptyUPA = new UsernamePasswordAuthenticationToken(null, null);
//            System.out.println(SecurityContextHolder.getContext().getAuthentication());
            // 将所有权限清空 因为没有携带有关jwt的参数
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        filterChain.doFilter(request,response);
    }

    private void setUpSpringAuthentiaction(Claims claims) {

        System.out.println(claims);
        ArrayList<String> originAuthorities = claims.get("authorities", ArrayList.class);
        System.out.println("authorities==>" + originAuthorities);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String authority : originAuthorities) {
            authorities.add(new SimpleGrantedAuthority(authority));
        }
//        val authorities = rawList.stream()
//                .map(String::valueOf)
//                .map(strAuthority -> new SimpleGrantedAuthority(strAuthority))
//                .collect(toList());
//        System.out.println("==> authorities" + authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(claims.getSubject(),null,authorities);

         SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     *  检查jwt是否在请求头当中
     * @param request
     * @return
     */
    private boolean checkJwtToken(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(appConfig.getJwt().getHeader());

        return authenticationHeader != null && authenticationHeader.startsWith(appConfig.getJwt().getPrefix());

    }

    private Optional<Claims> validateToken(HttpServletRequest request){
        String jwtToken = request.getHeader(appConfig.getJwt().getHeader()).replace(appConfig.getJwt().getPrefix(),"");
        try{
            return Optional.of(Jwts.parserBuilder().setSigningKey(JwtUtil.accesskey).build().parseClaimsJws(jwtToken).getBody());
        }catch (ExpiredJwtException | SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }



}
