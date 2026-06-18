package com.example.Ecommerce_YV.Service.Checkout;

import com.example.Ecommerce_YV.Dto.Checkout.DCheckoutItem;
import com.example.Ecommerce_YV.Dto.Checkout.DCheckoutRequest;
import com.example.Ecommerce_YV.Dto.Checkout.DCheckoutResponse;
import com.example.Ecommerce_YV.Entity.Cart;
import com.example.Ecommerce_YV.Entity.CartItem;
import com.example.Ecommerce_YV.Entity.Customer;
import com.example.Ecommerce_YV.Entity.EWallet;
import com.example.Ecommerce_YV.Entity.Order;
import com.example.Ecommerce_YV.Entity.OrderItems;
import com.example.Ecommerce_YV.Entity.Payment;
import com.example.Ecommerce_YV.Entity.Product;
import com.example.Ecommerce_YV.Entity.TransferBank;
import com.example.Ecommerce_YV.Repository.RCart;
import com.example.Ecommerce_YV.Repository.RCustomer;
import com.example.Ecommerce_YV.Repository.ROrder;
import com.example.Ecommerce_YV.Repository.ROrderItem;
import com.example.Ecommerce_YV.Repository.RProduct;
import com.example.Ecommerce_YV.Repository.Mainpage.RAddKeranjang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class SCheckout {

    private static final DateTimeFormatter CHECKOUT_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @Autowired
    private RCustomer customerRepository;

    @Autowired
    private RCart cartRepository;

    @Autowired
    private RAddKeranjang cartItemRepository;

    @Autowired
    private ROrder orderRepository;

    @Autowired
    private ROrderItem orderItemRepository;

    @Autowired
    private RProduct productRepository;

    @Transactional
    public DCheckoutResponse checkout(DCheckoutRequest request) {
        DCheckoutResponse response = new DCheckoutResponse();
        try {
            Optional<Customer> customerOptional = resolveCustomer(request.getCustomerId(), request.getCustomerName());
            if (customerOptional.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("Customer tidak ditemukan");
                return response;
            }

            Customer customer = customerOptional.get();
            Cart cart = cartRepository.findByUser_IdUser(customer.getIdUser()).orElse(null);
            List<DCheckoutItem> requestedItems = normalizeRequestedItems(request.getItems());
            List<CartItem> cartItems = cart == null ? List.of() : cartItemRepository.findByCart(cart);

            List<ResolvedCartLine> resolvedLines = resolveCheckoutLines(requestedItems, cartItems);
            if (resolvedLines.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("Keranjang masih kosong");
                return response;
            }

            String paymentMethod = normalizePaymentMethod(request.getPaymentMethod());
            if (!StringUtils.hasText(paymentMethod)) {
                paymentMethod = "EWALLET";
            }

            double totalPrice = 0.0;
            int totalItems = 0;
            for (ResolvedCartLine line : resolvedLines) {
                totalPrice += line.lineTotal;
                totalItems += line.quantity;
            }

            Payment paymentProcessor = createPaymentProcessor(paymentMethod);
            String paymentMessage = paymentProcessor.bayar(totalPrice);

            Order order = new Order();
            order.setUser(customer);
            order.setTanggal(new Date());
            order.setTotalHarga(totalPrice);
            order.setStatus("PAID");
            order = orderRepository.save(order);

            List<DCheckoutItem> checkoutItems = new ArrayList<>();
            for (ResolvedCartLine line : resolvedLines) {
                OrderItems orderItem = new OrderItems();
                orderItem.setOrder(order);
                orderItem.setProduct(line.product);
                orderItem.setJumlahBarang(line.quantity);
                orderItem.setTotalHarga(line.lineTotal);
                orderItem = orderItemRepository.save(orderItem);

                int currentStock = line.product.getStok() == null ? 0 : line.product.getStok();
                line.product.setStok(Math.max(0, currentStock - line.quantity));
                productRepository.save(line.product);

                DCheckoutItem checkoutItem = new DCheckoutItem();
                checkoutItem.setOrderItemId(orderItem.getIdOrderItem());
                checkoutItem.setProductId(line.product.getIdProduct());
                checkoutItem.setProductName(line.product.getNamaProduct());
                checkoutItem.setQuantity(line.quantity);
                checkoutItem.setUnitPrice((int) Math.round(line.unitPrice));
                checkoutItem.setTotalPrice(line.lineTotal);
                checkoutItems.add(checkoutItem);
            }

            if (cart != null) {
                cartItemRepository.deleteAll(cartItems);
                cartItemRepository.flush();
                cart.setJumlahBarang(0);
                cart.setTotalHarga(0.0);
                cartRepository.save(cart);
            }

            response.setSuccess(true);
            response.setMessage("Checkout berhasil");
            response.setOrderId(order.getIdOrder());
            response.setCustomerId(customer.getIdUser());
            response.setCustomerName(customer.getName());
            response.setPaymentMethod(displayPaymentMethod(paymentMethod));
            response.setPaymentMessage(paymentMessage);
            response.setCheckoutTime(LocalDateTime.now().format(CHECKOUT_TIME_FORMATTER));
            response.setTotalItems(totalItems);
            response.setTotalPrice(totalPrice);
            response.setItems(checkoutItems);
            return response;
        } catch (Exception exception) {
            response.setSuccess(false);
            response.setMessage("Checkout gagal: " + exception.getMessage());
            return response;
        }
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

    private Payment createPaymentProcessor(String paymentMethod) {
        if ("EWALLET".equals(paymentMethod)) {
            return new EWallet();
        }

        return new TransferBank();
    }

    private String normalizePaymentMethod(String paymentMethod) {
        String normalized = paymentMethod == null ? "" : paymentMethod.trim().toUpperCase(Locale.ROOT);

        if ("E-WALLET".equals(normalized) || "EWALLET".equals(normalized)) {
            return "EWALLET";
        }

        if ("TRANSFER".equals(normalized) || "TRANSFERBANK".equals(normalized) || "BANK".equals(normalized)) {
            return "TRANSFER";
        }

        return "";
    }

    private String displayPaymentMethod(String paymentMethod) {
        return "EWALLET".equals(paymentMethod) ? "EWallet" : "Transfer Bank";
    }

    private List<DCheckoutItem> normalizeRequestedItems(List<DCheckoutItem> requestedItems) {
        if (requestedItems == null) {
            return List.of();
        }

        return requestedItems.stream()
                .filter(item -> item != null && item.getProductId() != null && item.getQuantity() != null && item.getQuantity() > 0)
                .toList();
    }

    private List<ResolvedCartLine> resolveCheckoutLines(List<DCheckoutItem> requestedItems, List<CartItem> cartItems) {
        List<ResolvedCartLine> resolvedLines = new ArrayList<>();

        if (requestedItems != null && !requestedItems.isEmpty()) {
            for (DCheckoutItem requestedItem : requestedItems) {
                Product product = productRepository.findById(requestedItem.getProductId()).orElse(null);
                if (product == null) {
                    continue;
                }

                int quantity = requestedItem.getQuantity() == null ? 0 : requestedItem.getQuantity();
                if (quantity < 1) {
                    continue;
                }

                double unitPrice = product.getHarga() == null ? 0.0 : product.getHarga().doubleValue();
                resolvedLines.add(new ResolvedCartLine(product, quantity, unitPrice, unitPrice * quantity));
            }
        }

        if (!resolvedLines.isEmpty()) {
            return resolvedLines;
        }

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct() != null
                    ? productRepository.findById(cartItem.getProduct().getIdProduct()).orElse(null)
                    : null;

            if (product == null) {
                continue;
            }

            int quantity = cartItem.getJumlahBarang() == null ? 0 : cartItem.getJumlahBarang();
            if (quantity < 1) {
                continue;
            }

            double unitPrice = product.getHarga() == null ? 0.0 : product.getHarga().doubleValue();
            resolvedLines.add(new ResolvedCartLine(product, quantity, unitPrice, unitPrice * quantity));
        }

        return resolvedLines;
    }

    private static class ResolvedCartLine {
        private final Product product;
        private final int quantity;
        private final double unitPrice;
        private final double lineTotal;

        private ResolvedCartLine(Product product, int quantity, double unitPrice, double lineTotal) {
            this.product = product;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.lineTotal = lineTotal;
        }
    }
}
