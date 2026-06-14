package com.example.Ecommerce_YV.Entity;

public class EWallet implements Payment {

    @Override
    public void bayar(Double nominal) {
        System.out.println(
                "Pembayaran E-Wallet sebesar Rp " + nominal
        );
    }
}
