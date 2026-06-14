package com.example.Ecommerce_YV;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@SpringBootTest
class DatabaseSchemaTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void printTables() throws Exception {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SHOW TABLES")) {

            System.out.println("TABLES:");
            while (resultSet.next()) {
                System.out.println("- " + resultSet.getString(1));
            }
        }

        describeTable("users");
        describeTable("customers");
        describeTable("admins");
    }

    private void describeTable(String tableName) throws Exception {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("DESCRIBE " + tableName)) {

            System.out.println(tableName.toUpperCase() + " COLUMNS:");
            while (resultSet.next()) {
                System.out.println("- " + resultSet.getString("Field") + " | " + resultSet.getString("Type"));
            }
        }
    }
}
