package com.example.Ecommerce_YV.Repository;

import com.example.Ecommerce_YV.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RProduct extends JpaRepository<Product, Integer> {
}
