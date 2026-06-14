package com.example.Ecommerce_YV.Dto.Dashboard;

import java.util.List;

import com.example.Ecommerce_YV.Dto.Mainpage.Dsearch;
import com.example.Ecommerce_YV.Dto.Mainpage.Dsearch.ProductResponse;

public class DDashboard {
    public static class DashboardResponse {
        private List<ProductResponse> searchResults;
        private List<ProductResponse> randomProducts;

        public DashboardResponse(List<ProductResponse> searchResults, List<ProductResponse> randomProducts) {
            this.searchResults = searchResults;
            this.randomProducts = randomProducts;
        }

        public List<ProductResponse> getSearchResults() { return searchResults; }
        public void setSearchResults(List<ProductResponse> searchResults) { this.searchResults = searchResults; }
        
        public List<ProductResponse> getRandomProducts() { return randomProducts; }
        public void setRandomProducts(List<ProductResponse> randomProducts) { this.randomProducts = randomProducts; }
    }
}
