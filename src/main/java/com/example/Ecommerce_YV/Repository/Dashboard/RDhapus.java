package com.example.Ecommerce_YV.Repository.Dashboard;

import com.example.Ecommerce_YV.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RDhapus extends JpaRepository<Product, Integer> {
}
