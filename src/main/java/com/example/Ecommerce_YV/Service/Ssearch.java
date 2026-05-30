package com.example.Ecommerce_YV.Service;

import com.example.Ecommerce_YV.Dto.Dsearch;
import com.example.Ecommerce_YV.Repository.Rsearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Ssearch {

    @Autowired
    private Rsearch repository;

    public List<Dsearch.ProductResponse> getAllProducts(String search) {
        return repository.getAllProducts(search == null ? "" : search.trim());
    }
}
