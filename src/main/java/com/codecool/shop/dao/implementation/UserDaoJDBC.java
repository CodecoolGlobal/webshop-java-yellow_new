package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.UserDao;
import com.codecool.shop.datasource.dbConnection;
import com.codecool.shop.model.User;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBC implements UserDao {
    DataSource dataSource = dbConnection.getInstance().getDataSource();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    List<User> users = new ArrayList<>();
    private static UserDaoJDBC instance = null;

    public UserDaoJDBC() throws SQLException, IOException {
    }

    public static UserDaoJDBC getInstance() throws SQLException, IOException {
        if (instance == null) {
            instance = new UserDaoJDBC();
        }
        return instance;
    }

    @Override
    public void add(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, first_name, last_name, phone_number, email, billing_address, shipping_address) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getFirstName());
            preparedStatement.setString(4, user.getLastName());
            preparedStatement.setString(5, user.getPhone());
            preparedStatement.setString(6, user.getEmail());
            preparedStatement.setString(7, user.getBillingAddress());
            preparedStatement.setString(8, user.getShippingAddress());
            preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User find(int id) throws SQLException {

        Connection connection = dataSource.getConnection();
        preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE id=?");
        preparedStatement.setInt(1, id);
        resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            String username =resultSet.getString("username");
            String password = resultSet.getString("password");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String phone = resultSet.getString("phone_number");
            String email = resultSet.getString("email");
            String billingAddress = resultSet.getString("billing_address");
            String shippingAddress = resultSet.getString("shipping_address");
            User user =new User(username, password, firstName, lastName,email);
            user.setId(id);
            return user;
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
        return null;
    }

    @Override
    public void remove(int id) throws SQLException {
        Connection connection = dataSource.getConnection();
        preparedStatement = connection.prepareStatement("DELETE FROM users WHERE id=?");
        preparedStatement.setInt(1, id);
        preparedStatement.executeQuery();
        resultSet.close();
        preparedStatement.close();
        connection.close();
    }

    @Override
    public List<User> getAll() throws SQLException {
        users.clear();

        Connection connection = dataSource.getConnection();
        preparedStatement = connection.prepareStatement("SELECT * FROM users");
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String username =resultSet.getString("username");
            String password = resultSet.getString("password");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
//            String phone = resultSet.getString("phone_number");
            String email = resultSet.getString("email");
//            String billingAddress = resultSet.getString("billing_address");
//            String shippingAddress = resultSet.getString("shipping_address");
            User user =new User(username, password, firstName, lastName, email);
            user.setId(id);
            users.add(user);
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
        return users;
    }

    @Override
    public String getPasswordByUsername(String username) throws SQLException {
        String password = "";
        Connection connection = dataSource.getConnection();
        preparedStatement = connection.prepareStatement("SELECT password FROM users WHERE username=?");
        preparedStatement.setString(1, username);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            password = resultSet.getString("password");
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
        return password;

    }
}
