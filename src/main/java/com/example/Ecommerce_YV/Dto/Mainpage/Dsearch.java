package com.example.Ecommerce_YV.Dto.Mainpage;

public class Dsearch {
    public static class ProductResponse {
        private Integer id;
        private String name;
        private Integer price;
        private String category;
        private Integer stock;
        private String imageUrl;
        private Integer likeCount;

        public ProductResponse(Integer id, String name, Integer price, String category, Integer stock, String imageUrl, Integer likeCount) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.category = category;
            this.stock = stock;
            this.imageUrl = imageUrl;
            this.likeCount = likeCount;
        }

        public Integer getId() { return id; }
        public String getName() { return name; }
        public Integer getPrice() { return price; }
        public String getCategory() { return category; }
        public Integer getStock() { return stock; }
        public String getImageUrl() { return imageUrl; }
        public Integer getLikeCount() { return likeCount; }
    }
}
