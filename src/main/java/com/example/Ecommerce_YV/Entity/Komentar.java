package com.example.Ecommerce_YV.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "komentar")
@Getter
@Setter
@NoArgsConstructor
public class Komentar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_user")
    private Integer idUser;

    @Column(name = "Reply_to")
    private Integer replyTo;

    @Column(name = "id_product")
    private Integer idProduct;

    @Column(name = "name")
    private String name;

    @Column(name = "isi", columnDefinition = "TEXT", nullable = false)
    private String isi;

    @Column(name = "bintang")
    private Integer bintang;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (likeCount == null) {
            likeCount = 0;
        }
    }
}
