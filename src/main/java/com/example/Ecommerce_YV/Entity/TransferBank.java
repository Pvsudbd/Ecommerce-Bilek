package com.example.Ecommerce_YV.Entity;

public class TransferBank implements Payment {

    @Override
    public String bayar(Double nominal) {
        String message = "Pembayaran Transfer Bank sebesar Rp " + nominal;
        System.out.println(message);
        return message;
    }
}
