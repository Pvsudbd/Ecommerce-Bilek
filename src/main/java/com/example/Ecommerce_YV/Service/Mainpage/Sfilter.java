package com.example.Ecommerce_YV.Service.Mainpage;

import com.example.Ecommerce_YV.Dto.Mainpage.Dsearch;
import com.example.Ecommerce_YV.Repository.Mainpage.Rfilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Sfilter {

    @Autowired
    private Rfilter repository;

    public List<Dsearch.ProductResponse> getFilteredProducts(String search, String sort) {
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase();
        String normalizedSort = sort == null ? "default" : sort.trim().toLowerCase();

        List<Dsearch.ProductResponse> rawList = switch (normalizedSort) {
            case "name-desc" -> repository.findAllBySearchOrderByNameDesc(normalizedSearch);
            case "price-asc" -> repository.findAllBySearchOrderByPriceAsc(normalizedSearch);
            case "price-desc" -> repository.findAllBySearchOrderByPriceDesc(normalizedSearch);
            case "name-asc" -> repository.findAllBySearchOrderByNameAsc(normalizedSearch);
            case "default" -> repository.findAllBySearchOrderByRandom(normalizedSearch);
            default -> repository.findAllBySearchOrderByRandom(normalizedSearch);
        };

        return rawList.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        p -> p.getName() != null ? p.getName().toLowerCase().trim() : "",
                        java.util.LinkedHashMap::new, 
                        java.util.stream.Collectors.toList()
                ))
                .values().stream()
                .map(list -> {
                    Dsearch.ProductResponse first = list.get(0);
                    int totalStock = list.stream().mapToInt(p -> p.getStock() == null ? 0 : p.getStock()).sum();
                    return new Dsearch.ProductResponse(
                            first.getId(),
                            first.getName(),
                            first.getPrice(),
                            first.getCategory(),
                            totalStock,
                            first.getImageUrl(),
                            first.getLikeCount()
                    );
                })
                .collect(java.util.stream.Collectors.toList());
    }
}
