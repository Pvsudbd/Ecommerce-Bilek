package com.example.Ecommerce_YV.Service.Order;

import com.example.Ecommerce_YV.Dto.Order.DOrderItemResponse;
import com.example.Ecommerce_YV.Dto.Order.DOrderResponse;
import com.example.Ecommerce_YV.Entity.Customer;
import com.example.Ecommerce_YV.Entity.Order;
import com.example.Ecommerce_YV.Entity.OrderItems;
import com.example.Ecommerce_YV.Entity.Product;
import com.example.Ecommerce_YV.Repository.RCustomer;
import com.example.Ecommerce_YV.Repository.ROrder;
import com.example.Ecommerce_YV.Repository.ROrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class Sorder {

    private static final SimpleDateFormat ORDER_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private RCustomer customerRepository;

    @Autowired
    private ROrder orderRepository;

    @Autowired
    private ROrderItem orderItemRepository;

    public List<DOrderResponse> getOrders(Integer customerId, String customerName) {
        Optional<Customer> customerOptional = resolveCustomer(customerId, customerName);
        if (customerOptional.isEmpty()) {
            return List.of();
        }

        return orderRepository.findByUser(customerOptional.get()).stream()
                .sorted(Comparator.comparing(Order::getTanggal, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(this::toOrderResponse)
                .toList();
    }

    private Optional<Customer> resolveCustomer(Integer customerId, String customerName) {
        if (customerId != null && customerId > 0) {
            Optional<Customer> customerById = customerRepository.findById(customerId);
            if (customerById.isPresent()) {
                return customerById;
            }
        }

        if (!StringUtils.hasText(customerName)) {
            return Optional.empty();
        }

        return customerRepository.findByName(customerName.trim());
    }

    private DOrderResponse toOrderResponse(Order order) {
        List<OrderItems> orderItems = orderItemRepository.findByOrder(order);
        DOrderResponse response = new DOrderResponse();

        response.setOrderId(order.getIdOrder());
        response.setOrderDate(order.getTanggal() == null ? "" : ORDER_DATE_FORMAT.format(order.getTanggal()));
        response.setStatus(order.getStatus());
        response.setTotalPrice(order.getTotalHarga() == null ? 0.0 : order.getTotalHarga());
        response.setItems(orderItems.stream().map(this::toOrderItemResponse).toList());
        response.setTotalItems(response.getItems().stream()
                .mapToInt(item -> item.getQuantity() == null ? 0 : item.getQuantity())
                .sum());
        return response;
    }

    private DOrderItemResponse toOrderItemResponse(OrderItems orderItem) {
        Product product = orderItem.getProduct();
        DOrderItemResponse response = new DOrderItemResponse();

        response.setOrderItemId(orderItem.getIdOrderItem());
        response.setProductId(product == null ? null : product.getIdProduct());
        response.setProductName(product == null ? "Produk" : product.getNamaProduct());
        response.setQuantity(orderItem.getJumlahBarang() == null ? 0 : orderItem.getJumlahBarang());
        response.setTotalPrice(orderItem.getTotalHarga() == null ? 0.0 : orderItem.getTotalHarga());
        return response;
    }
}
