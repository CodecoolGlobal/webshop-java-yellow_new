package com.codecool.shop.dao;

import com.codecool.shop.model.Cart;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface CartDao {
    void add(int id) throws SQLException;
    void remove(int id) throws SQLException;
    void removeProduct(int id) throws SQLException;
    int get(int id) throws SQLException;
    void changeQuantity(int i, int id);
    List <Cart> getAll() throws SQLException;
    Map<Product, Integer> getAllDaoMem();
}
