package com.example.Ecommerce_YV.Repository.Dashboard;

import com.example.Ecommerce_YV.Dto.Mainpage.Dsearch;
import com.example.Ecommerce_YV.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RDashboard extends JpaRepository<Product, Integer> {

    // Mengambil 6 barang random dari database (Native MySQL)
    @Query(value = "SELECT * FROM products ORDER BY RAND() LIMIT 6", nativeQuery = true)
    List<Product> getRandomProducts();

    // Mencari barang berdasarkan nama atau kategori, mirip dengan Rsearch
    @Query("""
            SELECT new com.example.Ecommerce_YV.Dto.Dsearch$ProductResponse(
                p.idProduct,
                p.namaProduct,
                p.harga,
                p.kategori,
                p.stok,
                p.imageUrl,
                p.tLike
            )
            FROM Product p
            WHERE (
                LOWER(p.namaProduct) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.kategori) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            ORDER BY p.namaProduct ASC
            """)
    List<Dsearch.ProductResponse> searchProducts(@Param("search") String search);
}
