package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.CartDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.model.Cart;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CartDaoMem implements CartDao {
    private HashMap<Product, Integer> cart = new HashMap<>();
    private static CartDaoMem instance = null;

    /* A private Constructor prevents any other class from instantiating.
     */
    private CartDaoMem() {
    }

    public Map<Product, Integer> getCart() {
        return this.cart;
    }

    public static CartDaoMem getInstance() {
        if (instance == null) {
            instance = new CartDaoMem();
        }
        return instance;
    }

    @Override
    public void add(int id) {
        ProductDaoMem productsList = ProductDaoMem.getInstance();
        Product product = productsList.find(id);
            if(cart.containsKey(product)){
                cart.put(product, cart.get(product) + 1);
            }else{
                cart.put(product, 1);
            }
    }

    @Override
    public void remove(int id) {
        ProductDaoMem productsList = ProductDaoMem.getInstance();
        Product product = productsList.find(id);
        if(cart.containsKey(product)) {
            if(cart.get(product) == 1) {
                cart.remove(product);
            } else {
                cart.put(product, cart.get(product) - 1);
            }
        }
    }

    public void deleteCart(){
        this.cart = new HashMap<>();
    }

    public void changeQuantity(int id, int newQuantity) {
        ProductDao productList = ProductDaoMem.getInstance();
        Product product = null;
        try {
            product = productList.find(id);
        }catch(SQLException e){
            e.printStackTrace();
        }
        if(cart.containsKey(product)) {
            if (newQuantity == 0) {
                cart.remove(product);
            } else {
                cart.put(product, newQuantity);
            }
        }
    }

    @Override
    public List<Cart> getAll() throws SQLException {
        return null;
    }

    @Override
    public Map<Product, Integer> getAllDaoMem() {
        return this.cart;
    }

    @Override
    public void removeProduct(int id) throws SQLException {

    }

    @Override
    public int get(int id) throws SQLException {
        return 0;
    }
}
