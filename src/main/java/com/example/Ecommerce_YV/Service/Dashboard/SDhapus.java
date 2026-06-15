package com.example.Ecommerce_YV.Service.Dashboard;

import com.example.Ecommerce_YV.Entity.Product;
import com.example.Ecommerce_YV.Repository.RProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SDhapus {
    
    @Autowired
    private RProduct rProduct;

    @Transactional
    public void hapusProduk(Integer idProduct) {
        if (rProduct.existsById(idProduct)) {
            rProduct.deleteById(idProduct);
        } else {
            throw new RuntimeException("Produk tidak ditemukan");
        }
    }

    @Transactional
    public void tambahStok(Integer idProduct, Integer stokTambahan) {
        Product product = rProduct.findById(idProduct)
                .orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));
        
        // Menambahkan stok dari jumlah yang sudah ada
        Integer currentStock = product.getStok() == null ? 0 : product.getStok();
        product.setStok(currentStock + stokTambahan);
        
        rProduct.save(product);
    }
}
