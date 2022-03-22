package com.lmdd.pojo.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auth implements Serializable {
    private String accessToken;
    private String refreshToken;
}
