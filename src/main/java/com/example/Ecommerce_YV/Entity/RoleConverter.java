package com.example.Ecommerce_YV.Entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        if (role == null) {
            return null;
        }

        return switch (role) {
            case ADMIN -> "admin";
            case CUSTOMER -> "customer";
        };
    }

    @Override
    public Role convertToEntityAttribute(String dbValue) {
        if (dbValue == null) {
            return null;
        }

        return switch (dbValue.trim().toLowerCase()) {
            case "admin" -> Role.ADMIN;
            case "customer" -> Role.CUSTOMER;
            default -> Role.valueOf(dbValue.trim().toUpperCase());
        };
    }
}
