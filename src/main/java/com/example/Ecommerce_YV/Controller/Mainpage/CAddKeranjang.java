package com.example.Ecommerce_YV.Controller.Mainpage;

import com.example.Ecommerce_YV.Dto.Mainpage.DAddKeranjang;
import com.example.Ecommerce_YV.Dto.Mainpage.DCartResponse;
import com.example.Ecommerce_YV.Service.Mainpage.SAddKeranjang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CAddKeranjang {

    @Autowired
    private SAddKeranjang service;

    @PostMapping("/add")
    public DAddKeranjang addToCart(@RequestBody DAddKeranjang request) {
        return service.addToCart(request);
    }

    @GetMapping
    public DCartResponse getCart(
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) String customerName
    ) {
        return service.getCart(customerId, customerName);
    }

    @PatchMapping("/item")
    public DAddKeranjang updateCartItem(@RequestBody DAddKeranjang request) {
        return service.updateCartItem(request);
    }

    @DeleteMapping("/item")
    public DAddKeranjang removeCartItem(@RequestParam(required = false) Integer customerId,
                                         @RequestParam(required = false) String customerName,
                                         @RequestParam Integer productId) {
        DAddKeranjang request = new DAddKeranjang();
        request.setCustomerId(customerId);
        request.setCustomerName(customerName);
        request.setProductId(productId);
        request.setQuantity(0);
        return service.updateCartItem(request);
    }
}
