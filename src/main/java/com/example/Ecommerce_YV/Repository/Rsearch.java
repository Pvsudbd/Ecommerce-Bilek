package com.example.Ecommerce_YV.Repository;

import com.example.Ecommerce_YV.Dto.Dsearch;
import com.example.Ecommerce_YV.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Rsearch extends JpaRepository<Product, Integer> {

    @Query("""
            SELECT new com.example.Ecommerce_YV.Dto.Dsearch$ProductResponse(
                p.idProduct,
                p.namaProduct,
                p.harga,
                p.kategori,
                p.stok,
                p.imageUrl
            )
            FROM Product p
            WHERE (
                :search IS NULL
                OR :search = ''
                OR LOWER(p.namaProduct) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.kategori) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            ORDER BY p.namaProduct ASC
            """)
    List<Dsearch.ProductResponse> getAllProducts(@Param("search") String search);
}
