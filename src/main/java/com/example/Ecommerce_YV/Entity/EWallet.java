package com.example.Ecommerce_YV.Entity;

public class EWallet implements Payment {

    @Override
    public String bayar(Double nominal) {
        String message = "Pembayaran E-Wallet sebesar Rp " + nominal;
        System.out.println(message);
        return message;
    }
}
