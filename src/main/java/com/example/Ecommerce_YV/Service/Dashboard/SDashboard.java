package com.example.Ecommerce_YV.Service.Dashboard;

import com.example.Ecommerce_YV.Dto.Dashboard.DDashboard;
import com.example.Ecommerce_YV.Dto.Mainpage.Dsearch;
import com.example.Ecommerce_YV.Entity.Product;
import com.example.Ecommerce_YV.Repository.Dashboard.RDashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SDashboard {

    @Autowired
    private RDashboard repository;

    public DDashboard.DashboardResponse getDashboardData(String search) {
        // 1. Ambil barang random dari database
        List<Dsearch.ProductResponse> randomProductsRaw = repository.getRandomProducts().stream()
                .map(p -> new Dsearch.ProductResponse(
                        p.getIdProduct(),
                        p.getNamaProduct(),
                        p.getHarga(),
                        p.getKategori(),
                        p.getStok(),
                        p.getImageUrl(),
                        p.getTLike()
                )).collect(Collectors.toList());

        List<Dsearch.ProductResponse> randomProducts = deduplicateProducts(randomProductsRaw);

        // 2. Ambil hasil pencarian (jika parameter search ada)
        List<Dsearch.ProductResponse> searchResults = null;
        if (search != null && !search.trim().isEmpty()) {
            List<Dsearch.ProductResponse> searchResultsRaw = repository.searchProducts(search.trim());
            searchResults = deduplicateProducts(searchResultsRaw);
        }

        // 3. Kembalikan semua data sebagai satu Response DTO
        return new DDashboard.DashboardResponse(searchResults, randomProducts);
    }

    // Fungsi pembantu untuk mencegah nama yang sama muncul berkali-kali
    private List<Dsearch.ProductResponse> deduplicateProducts(List<Dsearch.ProductResponse> rawList) {
        if (rawList == null) return null;
        
        return rawList.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getName() != null ? p.getName().toLowerCase().trim() : "",
                        java.util.LinkedHashMap::new, // Mempertahankan urutan asli dari database
                        Collectors.toList()
                ))
                .values().stream()
                .map(list -> {
                    // Ambil detail dari produk pertama yang ditemukan
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
                .collect(Collectors.toList());
    }
}
