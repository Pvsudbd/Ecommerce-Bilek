package com.example.Ecommerce_YV.Repository;

import com.example.Ecommerce_YV.Entity.Order;
import com.example.Ecommerce_YV.Entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ROrderItem
        extends JpaRepository<OrderItems, Integer> {

    List<OrderItems> findByOrder(
            Order order
    );

}
