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
        
        productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Product not found"));

        if (request.getIdUser() == null) {
            throw new Exception("Wajib login untuk mengirim komentar!");
        }

        Komentar komentar = new Komentar();
        komentar.setIdProduct(productId);
        komentar.setIdUser(request.getIdUser());
        komentar.setReplyTo(request.getReplyTo());
        komentar.setName(request.getName() != null && !request.getName().isEmpty() ? request.getName() : "Anonymous");
        komentar.setIsi(request.getIsi());
        komentar.setBintang(request.getBintang() != null ? request.getBintang() : 5);
        
        return komentarRepository.save(komentar);
    }

    public void deleteComment(Integer idComment, Integer idUser, String role) throws Exception {
        Komentar komentar = komentarRepository.findById(idComment)
                .orElseThrow(() -> new Exception("Komentar tidak ditemukan"));

        if ("ADMIN".equalsIgnoreCase(role)) {
            // Admin bisa hapus komentar siapa saja
            komentarRepository.deleteById(idComment);
        } else if (idUser != null && idUser.equals(komentar.getIdUser())) {
            // User biasa cuma bisa hapus komentarnya sendiri
            komentarRepository.deleteById(idComment);
        } else {
            throw new Exception("Anda tidak memiliki izin untuk menghapus komentar ini!");
        }
    }
}
