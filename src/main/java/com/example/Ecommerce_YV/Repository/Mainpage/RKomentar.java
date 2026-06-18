package com.example.Ecommerce_YV.Repository.Mainpage;

import com.example.Ecommerce_YV.Entity.Komentar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RKomentar extends JpaRepository<Komentar, Integer> {
    List<Komentar> findByIdProductOrderByCreatedAtDesc(Integer idProduct);
}
