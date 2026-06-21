package com.example.Ecommerce_YV.Service.Dashboard;

import com.example.Ecommerce_YV.Entity.Product;
import com.example.Ecommerce_YV.Repository.Dashboard.RDashboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Service khusus buat ngurusin penambahan produk baru dari Dashboard Admin.
 * Tugas utamanya: Nerima gambar, dikirim ke Supabase Storage, 
 * dapet link-nya, lalu disimpen ke database MySQL.
 */

@Service
public class SDsupaimg {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Autowired
    private RDashboard rDashboard;

    // Fungsi utama buat nge-gas masukin produk ke database sekalian numpang upload gambar
    public void tambahProduk(String nama, Integer stok, Integer harga, String kategori, MultipartFile file) throws Exception {
        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = uploadImage(file);
        } else {
            imageUrl = "https://via.placeholder.com/300x300?text=No+Image";
        }

        Integer maxId = rDashboard.getMaxId();
        int newId = (maxId == null ? 0 : maxId) + 1;

        Product product = new Product();
        product.setIdProduct(newId);
        product.setNamaProduct(nama);
        product.setStok(stok);
        product.setHarga(harga);
        product.setKategori(kategori);
        product.setImageUrl(imageUrl);
        product.setTLike(0); // Default like 0

        rDashboard.save(product);
    }

    // Java bakal ngirim gambar ke supabase dengan ganti jadi link
    private String uploadImage(MultipartFile file) throws Exception {
        String extension = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        
        String filename = UUID.randomUUID().toString() + extension;
        String endpoint = supabaseUrl + "/storage/v1/object/products/" + filename;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.set("apikey", supabaseKey);
        headers.set("Content-Type", file.getContentType() != null ? file.getContentType() : "application/octet-stream");

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.POST, requestEntity, String.class);
            return supabaseUrl + "/storage/v1/object/public/products/" + filename;
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            throw new Exception("Gagal mengunggah gambar ke Supabase: " + e.getResponseBodyAsString());
        }
    }
}
