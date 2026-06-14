package com.example.Ecommerce_YV.Service.Mainpage;

import com.example.Ecommerce_YV.Dto.Mainpage.Dsearch;
import com.example.Ecommerce_YV.Repository.Rsearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Ssearch {

    @Autowired
    private Rsearch repository;

    public List<Dsearch.ProductResponse> getAllProducts(String search) {
        List<Dsearch.ProductResponse> rawList = repository.getAllProducts(search == null ? "" : search.trim());
        
        return rawList.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        p -> p.getName() != null ? p.getName().toLowerCase().trim() : "",
                        java.util.LinkedHashMap::new, // Mempertahankan urutan asli dari database
                        java.util.stream.Collectors.toList()
                ))
                .values().stream()
                .map(list -> {
                    // Ambil detail dari produk pertama
                    Dsearch.ProductResponse first = list.get(0);
                    // Gabungkan semua stok dari produk-produk duplikat
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
