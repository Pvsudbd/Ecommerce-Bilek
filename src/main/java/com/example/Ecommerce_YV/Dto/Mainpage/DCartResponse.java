package com.example.Ecommerce_YV.Dto.Mainpage;

import java.util.List;

public class DCartResponse {

    private Boolean success;

    private String message;

    private Integer customerId;

    private String customerName;

    private Integer cartId;

    private Integer totalItems;

    private Double totalPrice;

    private List<DCartItemResponse> items;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<DCartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<DCartItemResponse> items) {
        this.items = items;
    }
}
