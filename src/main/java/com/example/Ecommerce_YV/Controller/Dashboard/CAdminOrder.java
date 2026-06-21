package com.example.Ecommerce_YV.Controller.Dashboard;

import com.example.Ecommerce_YV.Dto.Dashboard.DAdminOrderStatusRequest;
import com.example.Ecommerce_YV.Dto.Order.DOrderResponse;
import com.example.Ecommerce_YV.Service.Dashboard.SAdminOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard/orders")
public class CAdminOrder {

    @Autowired
    private SAdminOrder service;

    @GetMapping
    public List<DOrderResponse> getAllOrders() {
        return service.getAllOrders();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Integer id,
            @RequestBody DAdminOrderStatusRequest request
    ) {
        boolean success = service.updateOrderStatus(id, request.getStatus());
        if (success) {
            return ResponseEntity.ok().body("{\"message\": \"Status pesanan berhasil diperbarui\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"message\": \"Pesanan tidak ditemukan\"}");
        }
    }
}
