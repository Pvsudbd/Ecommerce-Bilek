package com.example.Ecommerce_YV.Controller.Mainpage;

import com.example.Ecommerce_YV.Dto.Mainpage.DProductDetail;
import com.example.Ecommerce_YV.Service.Mainpage.SProductDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
public class CProductDetail {

    @Autowired
    private SProductDetail sProductDetail;

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductDetail(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(sProductDetail.getProductDetail(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<?> addComment(@PathVariable Integer id, @RequestBody DProductDetail.CommentRequest request) {
        try {
            return ResponseEntity.ok(sProductDetail.addComment(id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/comment/{idComment}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Integer idComment,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String role) {
        try {
            sProductDetail.deleteComment(idComment, userId, role);
            return ResponseEntity.ok("Komentar berhasil dihapus");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
