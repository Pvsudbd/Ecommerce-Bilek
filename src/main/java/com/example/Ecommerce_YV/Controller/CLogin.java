package com.example.Ecommerce_YV.Controller;

import com.example.Ecommerce_YV.Dto.Auth.DLoginRequest;
import com.example.Ecommerce_YV.Dto.Auth.DLoginResponse;
import com.example.Ecommerce_YV.Service.SAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class CLogin {

    @Autowired
    private SAuth authService;

    @PostMapping
    public DLoginResponse login(@RequestBody DLoginRequest request) {
        return authService.login(request);
    }
}
