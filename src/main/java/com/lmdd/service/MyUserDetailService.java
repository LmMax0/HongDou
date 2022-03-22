package com.lmdd.service;

import com.lmdd.dao.UserMapper;
import com.lmdd.pojo.login.Role;
//import com.lmdd.pojo.login.User;
import com.lmdd.pojo.login.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LM_MAX
 * @date 2022/3/15
 * 自定义的用户信息和权限处理
 */
@Slf4j
@Service("userDetailsService")
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    UserMapper mapper;
    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>) 返回的是一个包含用户信息和角色（可以多个）的信息
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = mapper.selectByUsernameUser(username);
        System.out.println("DEBUG =>" + user);
        if(user != null){
            List<Role> roleList = user.getRoles();
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            for (Role role : roleList) {
                if (role != null && role.getName()!=null) {
                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getName());
                    //1：此处将权限信息添加到 GrantedAuthority 对象中，在后面进行全权限验证时会使用GrantedAuthority对象。
                    grantedAuthorities.add(grantedAuthority);
                }
            }
          // user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
          // return user;
            return  user;
            // 把权限封装到 userdetails.User  中
           // return new org.springframework.security.core.userdetails.User(user.getUsername(),new BCryptPasswordEncoder().encode(user.getPassword()),grantedAuthorities);
//            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority();
        } else {
            // 抛出异常用户不存在
            throw new UsernameNotFoundException("admin: " + username + " do not exist!");
        }

    }

}
