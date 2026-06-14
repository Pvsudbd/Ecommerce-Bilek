package com.example.Ecommerce_YV.Repository;

import com.example.Ecommerce_YV.Entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class Rregister {

    @Autowired
    private RCustomer customerRepository;

    @Autowired
    private RUser userRepository;

    public boolean existsByName(String name) {
        return userRepository.existsByName(name);
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }
}
