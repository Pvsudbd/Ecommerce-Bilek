package com.example.Ecommerce_YV.Dto;

public class Dsearch {
    public static class ProductResponse {
        private Integer id;
        private String name;
        private Integer price;
        private String category;
        private Integer stock;
        private String imageUrl;

        public ProductResponse(Integer id, String name, Integer price, String category, Integer stock, String imageUrl) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.category = category;
            this.stock = stock;
            this.imageUrl = imageUrl;
        }

        public Integer getId() { return id; }
        public String getName() { return name; }
        public Integer getPrice() { return price; }
        public String getCategory() { return category; }
        public Integer getStock() { return stock; }
        public String getImageUrl() { return imageUrl; }
    }
}
