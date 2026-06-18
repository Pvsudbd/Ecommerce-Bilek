package com.example.Ecommerce_YV.Controller.Order;

import com.example.Ecommerce_YV.Dto.Order.DOrderResponse;
import com.example.Ecommerce_YV.Service.Order.Sorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class COrder {

    @Autowired
    private Sorder service;

    @GetMapping
    public List<DOrderResponse> getOrders(
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) String customerName
    ) {
        return service.getOrders(customerId, customerName);
    }
}
