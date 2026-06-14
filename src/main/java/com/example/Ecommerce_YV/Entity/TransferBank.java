package com.example.Ecommerce_YV.Entity;

public class TransferBank implements Payment {

    @Override
    public void bayar(Double nominal) {
        System.out.println(
                "Pembayaran Transfer Bank sebesar Rp " + nominal
        );
    }
}