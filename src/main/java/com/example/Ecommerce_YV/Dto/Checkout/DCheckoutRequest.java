package com.example.Ecommerce_YV.Dto.Checkout;

import java.util.List;

public class DCheckoutRequest {

    private Integer customerId;

    private String customerName;

    private String paymentMethod;

    private String shippingAddress;

    private List<DCheckoutItem> items;

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public List<DCheckoutItem> getItems() {
        return items;
    }

    public void setItems(List<DCheckoutItem> items) {
        this.items = items;
    }
}
