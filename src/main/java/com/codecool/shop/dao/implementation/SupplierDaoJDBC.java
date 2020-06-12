package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.datasource.dbConnection;
import com.codecool.shop.model.Supplier;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierDaoJDBC implements SupplierDao {
    DataSource dataSource = dbConnection.getInstance().getDataSource();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    List<Supplier> products = new ArrayList<>();
    private static SupplierDaoJDBC instance = null;

    public SupplierDaoJDBC() throws SQLException, IOException {
    }

    public static SupplierDaoJDBC getInstance() throws SQLException, IOException {
        if (instance == null) {
            instance = new SupplierDaoJDBC();
        }
        return instance;
    }

    @Override
    public void add(Supplier supplier) throws SQLException{

            Connection connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO supplier (name, description) VALUES (?,?)");
            preparedStatement.setString(1, supplier.getName());
            preparedStatement.setString(2, supplier.getDescription());
            preparedStatement.executeQuery();
            resultSet.close();
            preparedStatement.close();
            connection.close();
    }

    @Override
    public Supplier find(int id) throws SQLException{
            Connection connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM supplier WHERE id=?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String name =resultSet.getString("name");
                String description = resultSet.getString("description");
                Supplier supplier =new Supplier(name, description);
                supplier.setId(id);
                return supplier;
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        return null;
    }

    @Override
    public void remove(int id) throws SQLException{

            Connection connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM supplier WHERE id=?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeQuery();
            resultSet.close();
            preparedStatement.close();
            connection.close();
    }

    @Override
    public List<Supplier> getAll() throws SQLException{

            products.clear();
            Connection connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM supplier");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                Supplier supplier =new Supplier(name, description);
                supplier.setId(id);
                products.add(supplier);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return products;
    }
}
