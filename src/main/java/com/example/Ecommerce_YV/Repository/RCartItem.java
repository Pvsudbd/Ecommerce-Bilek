package com.example.Ecommerce_YV.Repository;

import com.example.Ecommerce_YV.Entity.Cart;
import com.example.Ecommerce_YV.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RCartItem
        extends JpaRepository<CartItem, Integer> {

    List<CartItem> findByCart(
            Cart cart
    );

}
