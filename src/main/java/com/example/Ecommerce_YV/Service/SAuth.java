package com.example.Ecommerce_YV.Service;

import com.example.Ecommerce_YV.Dto.Auth.DLoginRequest;
import com.example.Ecommerce_YV.Dto.Auth.DLoginResponse;
import com.example.Ecommerce_YV.Entity.Role;
import com.example.Ecommerce_YV.Entity.User;
import com.example.Ecommerce_YV.Repository.RCustomer;
import com.example.Ecommerce_YV.Repository.RUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SAuth {

    @Autowired
    private RUser userRepository;

    @Autowired
    private RCustomer customerRepository;

    public DLoginResponse login(DLoginRequest request) {
        DLoginResponse response = new DLoginResponse();

        Optional<User> userOptional = userRepository.findByName(request.getName());

        if (userOptional.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Username tidak ditemukan");
            return response;
        }

        User user = userOptional.get();

        if (!user.getPassword().equals(request.getPassword())) {
            response.setSuccess(false);
            response.setMessage("Password salah");
            return response;
        }

        response.setSuccess(true);
        response.setMessage("Login berhasil");
        response.setName(user.getName());
        response.setRole(user.getRole().name());

        if (user.getRole() == Role.CUSTOMER) {
            customerRepository.findByName(user.getName())
                    .ifPresent(customer -> response.setAddress(customer.getAddress()));
        }

        return response;
    }
}
