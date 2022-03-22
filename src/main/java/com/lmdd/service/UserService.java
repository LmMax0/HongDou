package com.lmdd.service;

import com.lmdd.dao.UserMapper;
import com.lmdd.dao.UserRolesMapper;
import com.lmdd.exception.LmException;
import com.lmdd.exception.LmExceptionEnum;
import com.lmdd.pojo.login.Auth;
import com.lmdd.pojo.login.User;
import com.lmdd.pojo.login.UserRolesKey;
import com.lmdd.utils.JwtUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sun.nio.cs.US_ASCII;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * @author LM_MAX
 * @date 2022/3/15
 * 要想从数据库中获取用户和用户权限可以继承一下UserDetailsService
 */
@Service
@RequiredArgsConstructor
public class UserService {

//    private JdbcUserDetailsManager manager = new JdbcUserDetailsManager();

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserRolesMapper userRolesMapper;

    @Autowired
    private final JwtUtil jwtUtil;

    @Value("${app.video-folder}")
    private String videoFolder;

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = new User();
//        user.setUsername(username);
//        return null;
//    }

    public Auth login(String username, String password){
        Auth auth = new Auth();
        User user = userMapper.selectByUsernameUser(username);
        if(user.getPassword().equals(password)){
            // 密码正确 授予token
            auth.setAccessToken(jwtUtil.creatAccessToken(user));
            auth.setRefreshToken(jwtUtil.createRefreshToken(user));
            return  auth;
        }
        return  auth;
    }

    public User register(User user) throws IOException {
        user.setRegistTime(new Date());
        int insert = userMapper.insert(user);
        if (insert > 0) {
            User userSelective = userMapper.selectByUsernameUser(user.getUsername());
            if(userSelective!=null){
                // 默认是用户
                userRolesMapper.insert(new UserRolesKey(1,user.getId()));

            }

            // 尝试创建用户空间目录
            try {
                Path targetFolder = Files.createDirectories(Paths.get(videoFolder, String.valueOf(user.getId())));
                Files.createDirectories(targetFolder);

            }catch (IOException e){
                e.printStackTrace();
            }


            return userSelective;
        }
        return  null;
    }

    public User getUserByName(String username){
        return userMapper.selectByUsernameUser(username);
    }
}
