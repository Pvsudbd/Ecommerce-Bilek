package com.example.Ecommerce_YV.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

public class Cart {


    @Column(name = "id_cart")
    private Integer idCart;
    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;
    public Integer getIdCart() {
        return idCart;
    }
    public void setIdCart(Integer idCart) {
        this.idCart = idCart;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
