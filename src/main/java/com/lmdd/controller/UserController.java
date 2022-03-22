package com.lmdd.controller;

import com.lmdd.pojo.login.Auth;
import com.lmdd.pojo.login.LoginDto;
import com.lmdd.pojo.login.Role;
import com.lmdd.pojo.login.User;
import com.lmdd.service.UserService;
import com.lmdd.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author LM_MAX
 * @date 2022/3/21
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    @ResponseBody
    public User register(@RequestBody User user) throws IOException {

        List<Role> roleList = new ArrayList<>();
        System.out.println(user);
        // 默认 用户权限为普通用户
        Role role = Role.builder().id(1).build();
        roleList.add(role);
        user.setRoles(roleList);
        user.setRegistTime(new Date());
        User register = userService.register(user);
        // 清除密码， 防止外泄
        register.setPassword(null);
        return register;
    }

    /**
     *  测试/user 接口连通性
     * @return 任意字段
     */
    @GetMapping("/user")
    @ResponseBody
    public String userMethod(){
        return "user";
    }


}
