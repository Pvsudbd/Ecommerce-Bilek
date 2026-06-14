package com.example.Ecommerce_YV.Controller;

import com.example.Ecommerce_YV.Dto.Mainpage.Dsearch;
import com.example.Ecommerce_YV.Service.Mainpage.Ssearch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class Csearch {

    @Autowired
    private Ssearch service;

    @GetMapping
    public List<Dsearch.ProductResponse> getProducts(@RequestParam(value = "search", required = false) String search) {
        return service.getAllProducts(search);
    }
}
