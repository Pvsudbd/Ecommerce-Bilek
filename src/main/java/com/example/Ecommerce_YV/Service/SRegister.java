package com.example.Ecommerce_YV.Service;

import com.example.Ecommerce_YV.Dto.Auth.DRegisterRequest;
import com.example.Ecommerce_YV.Dto.Auth.DRegisterResponse;
import com.example.Ecommerce_YV.Entity.Customer;
import com.example.Ecommerce_YV.Entity.Role;
import com.example.Ecommerce_YV.Repository.Rregister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SRegister {

    @Autowired
    private Rregister registerRepository;

    public DRegisterResponse register(DRegisterRequest request) {
        DRegisterResponse response = new DRegisterResponse();

        if (!StringUtils.hasText(request.getName())) {
            response.setSuccess(false);
            response.setMessage("Username wajib diisi");
            return response;
        }

        if (!StringUtils.hasText(request.getPassword())) {
            response.setSuccess(false);
            response.setMessage("Password wajib diisi");
            return response;
        }

        if (!StringUtils.hasText(request.getAddress())) {
            response.setSuccess(false);
            response.setMessage("Alamat wajib diisi");
            return response;
        }

        if (registerRepository.existsByName(request.getName().trim())) {
            response.setSuccess(false);
            response.setMessage("Username sudah digunakan");
            return response;
        }

        Customer customer = new Customer();
        customer.setName(request.getName().trim());
        customer.setPassword(request.getPassword());
        customer.setAddress(request.getAddress().trim());
        customer.setRole(Role.CUSTOMER);

        try {
            registerRepository.save(customer);
        } catch (DataAccessException exception) {
            response.setSuccess(false);
            response.setMessage("Gagal menyimpan data user. Periksa koneksi database.");
            return response;
        }

        response.setSuccess(true);
        response.setMessage("Register berhasil");
        response.setRole(Role.CUSTOMER.name());
        response.setName(customer.getName());
        response.setAddress(customer.getAddress());
        return response;
    }
}
