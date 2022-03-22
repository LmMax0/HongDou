package com.lmdd.controller;

import com.lmdd.pojo.login.Auth;
import com.lmdd.pojo.login.LoginDto;
import com.lmdd.pojo.login.Role;
import com.lmdd.pojo.login.User;
import com.lmdd.service.UserService;
import com.lmdd.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LM_MAX
 * @date 2022/3/15
 */
@Controller
public class TestController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/hello")
    @ResponseBody
    public String helloWorld(){
        return "helloWorld";
    }


    @GetMapping("/admin")
    @ResponseBody
    public String adminMethod(){
        return "admin";
    }

    @GetMapping("/login/do")
    @ResponseBody
    public String login(){
        return "login";
    }


//    @PostMapping("/root/token")
//    @ResponseBody
//    public Auth login(@Valid @RequestBody LoginDto loginDTO) {
//        return userService.login(loginDTO.getUsername(), loginDTO.getPassword());
//    }

    @PostMapping("/root/refreshToken")
    public Auth refreshToken(@RequestHeader(name = "Authorization") String authorization,
                             @RequestParam String refreshToken ) throws AccessDeniedException {
        final String prefix = "Bearer ";
        String accessToken = authorization.replace(prefix,"");
        if(jwtUtil.validateRefreshToken(refreshToken) && jwtUtil.validateAccessTokenWithoutExpiration(accessToken)){
            return new Auth(jwtUtil.createAccessTokenWithRefreshToken(refreshToken),accessToken);
        }
        throw new AccessDeniedException("访问被拒绝");
    }

//    @PostMapping("/register")
//    @ResponseBody
//    public User register(@RequestBody User user){
//
//        List<Role> roleList = new ArrayList<>();
//        System.out.println(user);
//        Role role = Role.builder().id(1).build();
//        roleList.add(role);
//        user.setRoles(roleList);
//
//        User register = userService.register(user);
//        register.setPassword(null);
//        return register;
//    }


}
