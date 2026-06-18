package com.example.Ecommerce_YV.Controller.Dashboard;

import com.example.Ecommerce_YV.Service.Dashboard.SDsupaimg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/dashboard/product")
public class CDSupaimg {

    @Autowired
    private SDsupaimg sDsupaimg;

    @PostMapping
    public ResponseEntity<?> addProduct(
            @RequestParam("nama") String nama,
            @RequestParam("stok") Integer stok,
            @RequestParam("harga") Integer harga,
            @RequestParam("kategori") String kategori,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            sDsupaimg.tambahProduk(nama, stok, harga, kategori, file);
            return ResponseEntity.ok("Produk berhasil ditambahkan");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Gagal menambahkan produk: " + e.getMessage());
        }
    }
}
