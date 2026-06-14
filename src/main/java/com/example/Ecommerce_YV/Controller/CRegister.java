package com.example.Ecommerce_YV.Controller;

import com.example.Ecommerce_YV.Dto.Auth.DRegisterRequest;
import com.example.Ecommerce_YV.Dto.Auth.DRegisterResponse;
import com.example.Ecommerce_YV.Service.Auth.SRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
public class CRegister {

    @Autowired
    private SRegister service;

    @PostMapping
    public DRegisterResponse register(@RequestBody DRegisterRequest request) {
        return service.register(request);
    }
}
