package com.example.Ecommerce_YV.Controller.Mainpage;

import com.example.Ecommerce_YV.Dto.Mainpage.DAddKeranjang;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = CAddKeranjang.class)
public class CartExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DAddKeranjang> handleValidation(MethodArgumentNotValidException exception) {
        DAddKeranjang response = new DAddKeranjang();
        response.setSuccess(false);
        response.setMessage("Data keranjang tidak valid");
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DAddKeranjang> handleAny(Exception exception) {
        DAddKeranjang response = new DAddKeranjang();
        response.setSuccess(false);
        response.setMessage("Gagal memproses keranjang: " + exception.getMessage());
        return ResponseEntity.ok(response);
    }
}
