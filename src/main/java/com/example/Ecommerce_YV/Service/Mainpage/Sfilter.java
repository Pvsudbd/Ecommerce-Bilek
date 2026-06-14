package com.example.Ecommerce_YV.Service.Mainpage;

import com.example.Ecommerce_YV.Dto.Mainpage.Dsearch;
import com.example.Ecommerce_YV.Repository.Rfilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Sfilter {

    @Autowired
    private Rfilter repository;

    public List<Dsearch.ProductResponse> getFilteredProducts(String search, String sort) {
        String normalizedSearch = search == null ? "" : search.trim();
        String normalizedSort = sort == null ? "default" : sort.trim().toLowerCase();

        return switch (normalizedSort) {
            case "name-desc" -> repository.findAllBySearchOrderByNameDesc(normalizedSearch);
            case "price-asc" -> repository.findAllBySearchOrderByPriceAsc(normalizedSearch);
            case "price-desc" -> repository.findAllBySearchOrderByPriceDesc(normalizedSearch);
            case "name-asc", "default" -> repository.findAllBySearchOrderByNameAsc(normalizedSearch);
            default -> repository.findAllBySearchOrderByNameAsc(normalizedSearch);
        };
    }
}
