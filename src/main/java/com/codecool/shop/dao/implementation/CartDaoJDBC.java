package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.CartDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.datasource.dbConnection;
import com.codecool.shop.model.Cart;
import com.codecool.shop.model.Product;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartDaoJDBC implements CartDao {
    private final DataSource dataSource = dbConnection.connect();
//    ProductDao productDao = new ProductDaoJDBC.getInstance();
    private static CartDaoJDBC instance = null;

    public CartDaoJDBC() throws IOException, SQLException {

    }
    public static CartDaoJDBC getInstance() throws IOException, SQLException {
        if(instance == null) {
            instance = new CartDaoJDBC();
        }
        return instance;
    }


    @Override
    public void add(int id) throws SQLException {
        Connection connection = dataSource.getConnection();
        String sql = "INSERT INTO product_cart (cart_id, product_id) VALUES (?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,1);
        preparedStatement.setInt(2,id); //here it needs the id of the product
        preparedStatement.executeUpdate();

        preparedStatement.close();
        connection.close();
        System.out.println("Product add id " + id);
    }

    @Override
    public void remove(int id) {

    }

    @Override
    public void changeQuantity(int i, int id) {

    }

    @Override
    public List<Cart> getAll() throws SQLException {
        List<Cart> templist = new ArrayList<>();
        Connection connection = dataSource.getConnection();
        String sql = "SELECT product_id,p.name, p.price, count(product_id), count(product_id) * p.price as total_price from product_cart\n" +
                "JOIN product p on product_cart.product_id = p.id\n" +
                "group by p.price, product_id, p.name";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            int productId = resultSet.getInt("product_id");
            String productName= resultSet.getString("name");
            float price = resultSet.getFloat("price");
            int quantity = resultSet.getInt("count");
            float total = resultSet.getFloat("total_price");

            Cart cart = new Cart(productId,productName,price,quantity,total);
            cart.setId(1);
            templist.add(cart);

        }

        return templist;
    }

    @Override
    public Map<Product, Integer> getAllDaoMem() {
        return null;
    }


}
