package com.lmdd.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmdd.common.ApiRestResponse;
import com.lmdd.pojo.login.Auth;
import com.lmdd.security.MyAuthProvider;
import com.lmdd.security.auth.ldap.LDAPMultiAuthenticationProvider;
import com.lmdd.security.auth.ldap.LDAPUserRepo;
import com.lmdd.security.filter.JwtFilter;
import com.lmdd.security.filter.MyLoginFilter;
import com.lmdd.service.UserService;
import com.lmdd.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LM_MAX
 * @date 2022/3/15
 */

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private LDAPUserRepo ldapUserRepo;
    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    UserService userService;
    @Autowired
    private MyAuthProvider myAuthProvider;
    /**
     * 指定加密方式
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        // 使用BCrypt加密密码
        return new BCryptPasswordEncoder();
    }


    @Override
    public void configure(WebSecurity web) throws Exception {

    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http

                .addFilterAt(restAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests(req -> req
                        .antMatchers("/root/**").permitAll()
//                        .antMatchers("/user").hasRole("user")
//                        .antMatchers("/admin").hasRole("admin")
//                        .antMatchers("/dologin").permitAll()
//                        .antMatchers("/").hasRole("user")
                )
                .csrf(c -> c.disable())
                .formLogin(form -> form
                        .loginProcessingUrl("/user/login")
                )
                .logout( out -> out
                        .logoutUrl("/logout")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout","POST"))
                        .logoutSuccessUrl("/")
                        .clearAuthentication(true)

                );
    }

    private MyLoginFilter restAuthenticationFilter() throws Exception{
        log.info("LoginFilter 被调用");
        MyLoginFilter myLoginFilter = new MyLoginFilter();
        ProviderManager providerManager = new ProviderManager(Collections.singletonList(myAuthProvider));
        myLoginFilter.setAuthenticationManager(providerManager);
        myLoginFilter.setAuthenticationSuccessHandler(jsonLoginSuccessHandler());
        myLoginFilter.setFilterProcessesUrl("/user/login");
        return myLoginFilter;
    }

    private AuthenticationSuccessHandler jsonLoginSuccessHandler() {
      //  User user = userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
        // 登录成功后分发token
//        com.lmdd.pojo.login.User principal = (com.lmdd.pojo.login.User ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return (req, res, auth) -> {
            com.lmdd.pojo.login.User principal = (com.lmdd.pojo.login.User ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            res.setContentType("application/json;charset=utf-8");
            PrintWriter out = res.getWriter();
            Map map = new HashMap();
            map.put("username",auth.getName());
            map.put("tip","登录成功");
            map.put("nickname",principal.getNickname());
            map.put("auth",userService.login(auth.getName(),principal.getPassword()));
            out.write(new ObjectMapper().writeValueAsString(ApiRestResponse.success(map)));
            out.flush();
            out.close();
        };
    }



    // 注册一个ldap的 provider
    @Bean
    LDAPMultiAuthenticationProvider ldapProvider(){
        return new LDAPMultiAuthenticationProvider(ldapUserRepo);
    }

//    @Bean
//    MyAuthProvider myAuthProvider() {return new MyAuthProvider();};

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
        auth.authenticationProvider(ldapProvider());

//        auth.authenticationProvider(myAuthProvider);
    }


}
