package com.example.Ecommerce_YV.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "id_user")
public class Admin extends User {
}

