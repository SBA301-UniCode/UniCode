package com.example.unicode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbChecker {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5433/unicode";
        String user = "admin";
        String password = "admin123";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
             
            ResultSet rs = stmt.executeQuery("SELECT document_id, title, document_url FROM document ORDER BY document_id DESC LIMIT 5");
            while (rs.next()) {
                System.out.println("ID: " + rs.getString("document_id"));
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("URL: " + rs.getString("document_url"));
                System.out.println("---------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
