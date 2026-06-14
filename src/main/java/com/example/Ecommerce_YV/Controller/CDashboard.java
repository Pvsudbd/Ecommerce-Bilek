package com.example.Ecommerce_YV.Controller;

import com.example.Ecommerce_YV.Dto.Dashboard.DDashboard;
import com.example.Ecommerce_YV.Service.Dashboard.SDashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class CDashboard {

    @Autowired
    private SDashboard service;

    @Autowired
    private com.example.Ecommerce_YV.Service.Dashboard.SDhapus sDhapus;

    @GetMapping
    public DDashboard.DashboardResponse getDashboard(
            @RequestParam(value = "search", required = false) String search) {
        return service.getDashboardData(search);
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public org.springframework.http.ResponseEntity<?> deleteProduct(@org.springframework.web.bind.annotation.PathVariable Integer id) {
        try {
            sDhapus.hapusProduk(id);
            return org.springframework.http.ResponseEntity.ok("Produk berhasil dihapus");
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @org.springframework.web.bind.annotation.PatchMapping("/{id}/stock")
    public org.springframework.http.ResponseEntity<?> updateStock(
            @org.springframework.web.bind.annotation.PathVariable Integer id,
            @org.springframework.web.bind.annotation.RequestBody java.util.Map<String, Integer> payload) {
        try {
            Integer stokTambahan = payload.get("jumlah");
            if (stokTambahan == null) {
                return org.springframework.http.ResponseEntity.badRequest().body("Parameter 'jumlah' diperlukan");
            }
            sDhapus.tambahStok(id, stokTambahan);
            return org.springframework.http.ResponseEntity.ok("Stok berhasil ditambahkan");
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
