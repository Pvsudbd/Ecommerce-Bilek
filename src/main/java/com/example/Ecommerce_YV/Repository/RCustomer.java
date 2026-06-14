package com.example.Ecommerce_YV.Repository;

import com.example.Ecommerce_YV.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RCustomer extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByName(String name);

    boolean existsByName(String name);

}
