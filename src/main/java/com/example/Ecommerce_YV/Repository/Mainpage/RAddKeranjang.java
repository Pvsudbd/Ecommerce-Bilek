package com.example.Ecommerce_YV.Repository.Mainpage;

import com.example.Ecommerce_YV.Entity.Cart;
import com.example.Ecommerce_YV.Entity.CartItem;
import com.example.Ecommerce_YV.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RAddKeranjang extends JpaRepository<CartItem, Integer> {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    Optional<CartItem> findByCart_IdCartAndProduct_IdProduct(Integer cartId, Integer productId);

    List<CartItem> findByCart(Cart cart);
}
