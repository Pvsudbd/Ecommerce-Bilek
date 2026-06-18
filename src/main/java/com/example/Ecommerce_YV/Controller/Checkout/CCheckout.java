package com.example.Ecommerce_YV.Controller.Checkout;

import com.example.Ecommerce_YV.Dto.Checkout.DCheckoutRequest;
import com.example.Ecommerce_YV.Dto.Checkout.DCheckoutResponse;
import com.example.Ecommerce_YV.Service.Checkout.SCheckout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
public class CCheckout {

    @Autowired
    private SCheckout service;

    @PostMapping("/submit")
    public DCheckoutResponse checkout(@RequestBody DCheckoutRequest request) {
        return service.checkout(request);
    }
}
