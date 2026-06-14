package com.example.Ecommerce_YV;

import com.example.Ecommerce_YV.Dto.Auth.DRegisterRequest;
import com.example.Ecommerce_YV.Dto.Auth.DRegisterResponse;
import com.example.Ecommerce_YV.Service.SRegister;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RegisterIntegrationTest {

    @Autowired
    private SRegister registerService;

    @Test
    void registerWithSpecialCharacterPassword() {
        DRegisterRequest request = new DRegisterRequest();
        request.setName("debug_user_" + System.currentTimeMillis());
        request.setPassword("waguricantik225#");
        request.setAddress("Jl Debug No 1");

        try {
            DRegisterResponse response = registerService.register(request);
            System.out.println("SUCCESS: " + response.getSuccess() + " - " + response.getMessage());
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        }
    }
}
