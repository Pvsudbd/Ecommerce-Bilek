package com.example.Ecommerce_YV.Repository;

import com.example.Ecommerce_YV.Dto.Mainpage.Dsearch;
import com.example.Ecommerce_YV.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Rfilter extends JpaRepository<Product, Integer> {

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
                :search IS NULL
                OR :search = ''
                OR LOWER(p.namaProduct) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.kategori) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            ORDER BY LOWER(p.namaProduct) ASC
            """)
    List<Dsearch.ProductResponse> findAllBySearchOrderByNameAsc(@Param("search") String search);

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
                :search IS NULL
                OR :search = ''
                OR LOWER(p.namaProduct) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.kategori) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            ORDER BY LOWER(p.namaProduct) DESC
            """)
    List<Dsearch.ProductResponse> findAllBySearchOrderByNameDesc(@Param("search") String search);

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
                :search IS NULL
                OR :search = ''
                OR LOWER(p.namaProduct) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.kategori) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            ORDER BY p.harga ASC
            """)
    List<Dsearch.ProductResponse> findAllBySearchOrderByPriceAsc(@Param("search") String search);

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
                :search IS NULL
                OR :search = ''
                OR LOWER(p.namaProduct) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.kategori) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            ORDER BY p.harga DESC
            """)
    List<Dsearch.ProductResponse> findAllBySearchOrderByPriceDesc(@Param("search") String search);
}
