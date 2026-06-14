package com.example.Ecommerce_YV.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @Column(name = "id_product")
    private Integer idProduct;

    @Column(name = "nama_product")
    private String namaProduct;

    @Column(name = "stok")
    private Integer stok;

    @Column(name = "harga")
    private Integer harga;

    @Column(name = "kategori")
    private String kategori;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "T_like")
    private Integer tLike;
}
