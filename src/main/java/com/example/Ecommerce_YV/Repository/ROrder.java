package com.example.Ecommerce_YV.Repository;

import com.example.Ecommerce_YV.Entity.Order;
import com.example.Ecommerce_YV.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ROrder extends JpaRepository<Order, Integer> {
    List<Order> findByUser(
            User user
    );

}
