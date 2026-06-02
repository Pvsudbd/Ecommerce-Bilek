package com.example.Ecommerce_YV.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Setter;

import java.util.Date;

@Table(name = "order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order")
    private Integer idOrder;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @Setter
    @Column(name = "date")
    private Date date;

    @Setter
    @Column(name = "total_harga")
    private Double totalHarga;

    @Column(name = "status", length = 50)
    private String status;

//  getter setter

