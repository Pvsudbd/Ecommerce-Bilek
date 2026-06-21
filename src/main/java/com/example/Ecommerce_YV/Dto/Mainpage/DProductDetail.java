package com.example.Ecommerce_YV.Dto.Mainpage;

import com.example.Ecommerce_YV.Entity.Komentar;
import com.example.Ecommerce_YV.Entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class DProductDetail {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Product product;
        private List<Komentar> comments;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentRequest {
        private Integer idUser;
        private String name;
        private String isi;
        private Integer bintang;
        private Integer replyTo;
    }
}
