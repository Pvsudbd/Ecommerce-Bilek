package com.example.Ecommerce_YV.Controller.Mainpage;

import com.example.Ecommerce_YV.Dto.Mainpage.Dsearch;
import com.example.Ecommerce_YV.Service.Mainpage.Sfilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products/filter")
public class Cfilter {

    @Autowired
    private Sfilter service;

    @GetMapping
    public List<Dsearch.ProductResponse> getFilteredProducts(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        return service.getFilteredProducts(search, sort);
    }
}
