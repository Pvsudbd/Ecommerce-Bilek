package com.example.Ecommerce_YV.Service.Mainpage;

import com.example.Ecommerce_YV.Dto.Mainpage.DAddKeranjang;
import com.example.Ecommerce_YV.Dto.Mainpage.DCartItemResponse;
import com.example.Ecommerce_YV.Dto.Mainpage.DCartResponse;
import com.example.Ecommerce_YV.Entity.Cart;
import com.example.Ecommerce_YV.Entity.CartItem;
import com.example.Ecommerce_YV.Entity.Customer;
import com.example.Ecommerce_YV.Entity.Product;
import com.example.Ecommerce_YV.Repository.RCart;
import com.example.Ecommerce_YV.Repository.RCustomer;
import com.example.Ecommerce_YV.Repository.RProduct;
import com.example.Ecommerce_YV.Repository.Mainpage.RAddKeranjang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SAddKeranjang {

    @Autowired
    private RCustomer customerRepository;

    @Autowired
    private RProduct productRepository;

    @Autowired
    private RCart cartRepository;

    @Autowired
    private RAddKeranjang cartItemRepository;

    @Transactional
    public DAddKeranjang addToCart(DAddKeranjang request) {
        DAddKeranjang response = new DAddKeranjang();

        Integer customerId = request.getCustomerId();
        String customerName = request.getCustomerName() == null ? "" : request.getCustomerName().trim();
        Integer productId = request.getProductId();
        int quantityToAdd = request.getQuantity() == null ? 1 : request.getQuantity();

        if (productId == null) {
            response.setSuccess(false);
            response.setMessage("Produk tidak ditemukan");
            return response;
        }

        if (quantityToAdd < 1) {
            response.setSuccess(false);
            response.setMessage("Jumlah barang minimal 1");
            return response;
        }

        Optional<Customer> customerOptional = customerId != null
                ? customerRepository.findById(customerId)
                : customerRepository.findByName(customerName);

        if (customerOptional.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Customer tidak ditemukan");
            return response;
        }

        Customer customer = customerOptional.get();
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Produk tidak ditemukan");
            return response;
        }

        Product product = productOptional.get();
        int availableStock = product.getStok() == null ? 0 : product.getStok();
        if (availableStock < 1) {
            response.setSuccess(false);
            response.setMessage("Stok produk habis");
            return response;
        }

        Cart cart = cartRepository.findByUser_IdUser(customer.getIdUser())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(customer);
                    newCart.setJumlahBarang(0);
                    newCart.setTotalHarga(0.0);
                    return cartRepository.save(newCart);
                });

        Optional<CartItem> existingItemOptional = cartItemRepository.findByCart_IdCartAndProduct_IdProduct(
                cart.getIdCart(),
                product.getIdProduct()
        );
        CartItem cartItem = existingItemOptional.orElseGet(CartItem::new);
        int currentQuantity = cartItem.getJumlahBarang() == null ? 0 : cartItem.getJumlahBarang();
        int newQuantity = currentQuantity + quantityToAdd;

        if (newQuantity > availableStock) {
            response.setSuccess(false);
            response.setMessage("Jumlah barang melebihi stok yang tersedia");
            return response;
        }

        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setJumlahBarang(newQuantity);
        cartItem.setTotalHarga(newQuantity * (product.getHarga() == null ? 0.0 : product.getHarga().doubleValue()));
        cartItemRepository.save(cartItem);

        refreshCartSummary(cart);

        response.setSuccess(true);
        response.setMessage(existingItemOptional.isPresent()
                ? "Jumlah barang di keranjang diperbarui"
                : "Barang berhasil ditambahkan ke keranjang");
        response.setCustomerName(customer.getName());
        response.setProductId(product.getIdProduct());
        response.setProductName(product.getNamaProduct());
        response.setQuantity(newQuantity);
        response.setTotalItems(cart.getJumlahBarang());
        response.setTotalPrice(cart.getTotalHarga());
        response.setRemainingStock(availableStock - newQuantity);
        return response;
    }

    @Transactional(readOnly = true)
    public DCartResponse getCart(Integer customerId, String customerName) {
        DCartResponse response = new DCartResponse();

        Optional<Customer> customerOptional = resolveCustomer(customerId, customerName);
        if (customerOptional.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Customer tidak ditemukan");
            response.setItems(List.of());
            return response;
        }

        Customer customer = customerOptional.get();
        Optional<Cart> cartOptional = cartRepository.findByUser_IdUser(customer.getIdUser());

        if (cartOptional.isEmpty()) {
            response.setSuccess(true);
            response.setMessage("Keranjang masih kosong");
            response.setCustomerId(customer.getIdUser());
            response.setCustomerName(customer.getName());
            response.setItems(List.of());
            response.setTotalItems(0);
            response.setTotalPrice(0.0);
            return response;
        }

        Cart cart = cartOptional.get();
        List<CartItem> items = cartItemRepository.findByCart(cart);

        populateCartResponse(response, customer, cart, items);
        response.setSuccess(true);
        response.setMessage(items.isEmpty() ? "Keranjang masih kosong" : "Keranjang berhasil dimuat");
        return response;
    }

    @Transactional
    public DAddKeranjang updateCartItem(DAddKeranjang request) {
        DAddKeranjang response = new DAddKeranjang();

        Integer customerId = request.getCustomerId();
        String customerName = request.getCustomerName() == null ? "" : request.getCustomerName().trim();
        Integer productId = request.getProductId();
        int requestedQuantity = request.getQuantity() == null ? 0 : request.getQuantity();

        if (productId == null) {
            response.setSuccess(false);
            response.setMessage("Produk tidak ditemukan");
            return response;
        }

        Optional<Customer> customerOptional = resolveCustomer(customerId, customerName);
        if (customerOptional.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Customer tidak ditemukan");
            return response;
        }

        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Produk tidak ditemukan");
            return response;
        }

        Customer customer = customerOptional.get();
        Product product = productOptional.get();
        int availableStock = product.getStok() == null ? 0 : product.getStok();
        Cart cart = getOrCreateCart(customer);
        Optional<CartItem> existingItemOptional = cartItemRepository.findByCart_IdCartAndProduct_IdProduct(
                cart.getIdCart(),
                product.getIdProduct()
        );

        if (requestedQuantity <= 0) {
            if (existingItemOptional.isPresent()) {
                cartItemRepository.delete(existingItemOptional.get());
                cartItemRepository.flush();
                refreshCartSummary(cart);
            }

            response.setSuccess(true);
            response.setMessage("Barang berhasil dihapus dari keranjang");
            response.setCustomerName(customer.getName());
            response.setProductId(product.getIdProduct());
            response.setProductName(product.getNamaProduct());
            response.setQuantity(0);
            response.setTotalItems(cart.getJumlahBarang());
            response.setTotalPrice(cart.getTotalHarga());
            response.setRemainingStock(availableStock);
            return response;
        }

        if (requestedQuantity > availableStock) {
            response.setSuccess(false);
            response.setMessage("Jumlah barang melebihi stok yang tersedia");
            return response;
        }

        CartItem cartItem = existingItemOptional.orElseGet(CartItem::new);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setJumlahBarang(requestedQuantity);
        cartItem.setTotalHarga(requestedQuantity * (product.getHarga() == null ? 0.0 : product.getHarga().doubleValue()));
        cartItemRepository.save(cartItem);

        refreshCartSummary(cart);

        response.setSuccess(true);
        response.setMessage(existingItemOptional.isPresent()
                ? "Jumlah barang di keranjang diperbarui"
                : "Barang berhasil ditambahkan ke keranjang");
        response.setCustomerName(customer.getName());
        response.setProductId(product.getIdProduct());
        response.setProductName(product.getNamaProduct());
        response.setQuantity(requestedQuantity);
        response.setTotalItems(cart.getJumlahBarang());
        response.setTotalPrice(cart.getTotalHarga());
        response.setRemainingStock(availableStock - requestedQuantity);
        return response;
    }

    private void refreshCartSummary(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCart(cart);

        int totalItems = items.stream()
                .mapToInt(item -> item.getJumlahBarang() == null ? 0 : item.getJumlahBarang())
                .sum();

        double totalPrice = items.stream()
                .mapToDouble(item -> item.getTotalHarga() == null ? 0.0 : item.getTotalHarga())
                .sum();

        cart.setJumlahBarang(totalItems);
        cart.setTotalHarga(totalPrice);
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(Customer customer) {
        return cartRepository.findByUser_IdUser(customer.getIdUser())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(customer);
                    newCart.setJumlahBarang(0);
                    newCart.setTotalHarga(0.0);
                    return cartRepository.save(newCart);
                });
    }

    private Optional<Customer> resolveCustomer(Integer customerId, String customerName) {
        if (customerId != null) {
            return customerRepository.findById(customerId);
        }

        if (customerName == null || customerName.isBlank()) {
            return Optional.empty();
        }

        return customerRepository.findByName(customerName.trim());
    }

    private void populateCartResponse(DCartResponse response, Customer customer, Cart cart, List<CartItem> items) {
        response.setCustomerId(customer.getIdUser());
        response.setCustomerName(customer.getName());
        response.setCartId(cart.getIdCart());

        int totalItems = items.stream()
                .mapToInt(item -> item.getJumlahBarang() == null ? 0 : item.getJumlahBarang())
                .sum();

        double totalPrice = items.stream()
                .mapToDouble(item -> item.getTotalHarga() == null ? 0.0 : item.getTotalHarga())
                .sum();

        response.setTotalItems(totalItems);
        response.setTotalPrice(totalPrice);
        response.setItems(items.stream()
                .map(this::toCartItemResponse)
                .toList());
    }

    private DCartItemResponse toCartItemResponse(CartItem item) {
        DCartItemResponse response = new DCartItemResponse();
        Product product = item.getProduct();

        response.setCartItemId(item.getIdCartItem());
        response.setProductId(product == null ? null : product.getIdProduct());
        response.setProductName(product == null ? null : product.getNamaProduct());
        response.setQuantity(item.getJumlahBarang() == null ? 0 : item.getJumlahBarang());
        response.setUnitPrice(product == null || product.getHarga() == null ? 0.0 : product.getHarga().doubleValue());
        response.setTotalPrice(item.getTotalHarga() == null ? 0.0 : item.getTotalHarga());
        response.setAvailableStock(product == null || product.getStok() == null ? 0 : product.getStok());
        return response;
    }
}
