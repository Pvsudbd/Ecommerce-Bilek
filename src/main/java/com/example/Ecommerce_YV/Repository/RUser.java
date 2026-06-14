package com.example.Ecommerce_YV.Repository;

import com.example.Ecommerce_YV.Entity.User;
import com.example.Ecommerce_YV.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RUser extends JpaRepository<User, Integer> {
    Optional<User> findByName(String name);

    boolean existsByName(String name);

    List<User> findByRole(Role role);
}

