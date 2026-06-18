package com.example.Ecommerce_YV.Repository;

import com.example.Ecommerce_YV.Entity.Cart;
import com.example.Ecommerce_YV.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RCart extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser(
            User user
    );

    Optional<Cart> findByUser_IdUser(Integer idUser);

}

