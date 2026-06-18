package com.example.Ecommerce_YV.Service.Mainpage;

import com.example.Ecommerce_YV.Dto.Mainpage.DProductDetail;
import com.example.Ecommerce_YV.Entity.Komentar;
import com.example.Ecommerce_YV.Entity.Product;
import com.example.Ecommerce_YV.Repository.Mainpage.Rfilter;
import com.example.Ecommerce_YV.Repository.Mainpage.RKomentar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SProductDetail {

    @Autowired
    private Rfilter productRepository;

    @Autowired
    private RKomentar komentarRepository;

    public DProductDetail.Response getProductDetail(Integer productId) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Product not found"));

        List<Komentar> comments = komentarRepository.findByIdProductOrderByCreatedAtDesc(productId);

        return new DProductDetail.Response(product, comments);
    }

    public Komentar addComment(Integer productId, DProductDetail.CommentRequest request) throws Exception {
        // Validate product exists
        productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Product not found"));

        Komentar komentar = new Komentar();
        komentar.setIdProduct(productId);
        komentar.setIdUser(request.getIdUser()); // Bisa null jika guest
        komentar.setName(request.getName() != null && !request.getName().isEmpty() ? request.getName() : "Anonymous");
        komentar.setIsi(request.getIsi());
        komentar.setBintang(request.getBintang() != null ? request.getBintang() : 5);
        
        return komentarRepository.save(komentar);
    }
}
