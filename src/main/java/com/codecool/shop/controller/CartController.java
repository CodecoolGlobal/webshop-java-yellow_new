package com.codecool.shop.controller;

import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.dao.CartDao;
import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.implementation.CartDaoJDBC;
import com.codecool.shop.dao.implementation.CartDaoMem;
import com.codecool.shop.dao.implementation.ProductCategoryDaoMem;
import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.model.Cart;
import com.codecool.shop.model.Product;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/cart"})
public class CartController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //It's a LIST
        CartDao cartDao = null;
        try {
            cartDao = CartDaoJDBC.getInstance();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());
////        Map<Product, Integer> cartMap = null;
//        cartMap = cartDao.getAll();
//
//        int numberOfProducts = 0;
//        double sum = 0;
//        for (Map.Entry<Product, Integer> entry : cartMap.entrySet()) {
//            sum += entry.getKey().getPriceDouble() * entry.getValue();
//            numberOfProducts += entry.getValue();
//        }
        List<Cart> templist = new ArrayList<>();
        try {
            templist = cartDao.getAll();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        float sum = 0;
        int numberOfProducts = 0;
        for (int i = 0; i < templist.size(); i++) {
            sum += templist.get(i).getTotal();
            numberOfProducts += templist.get(i).getQuantity();
        }
        context.setVariable("cartList", templist);
        context.setVariable("totalPrice", sum);
        context.setVariable("totalNumberOfItems", numberOfProducts);

//        System.out.println(templist);

        engine.process("product/cart_page.html", context, resp.getWriter());

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CartDao carDao = null;
        try {
            carDao = CartDaoJDBC.getInstance();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            if (req.getParameter("addToCart") != null) {
                String id = req.getParameter("addToCart");
                carDao.add(Integer.parseInt(id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (req.getParameter("itemId") != null && req.getParameter("changeQuantity") != null) {
                String itemIdToChangeQuantity = req.getParameter("itemId");
                String newQuantity = req.getParameter("changeQuantity");
                if (Integer.parseInt(newQuantity) == 0) {
                    carDao.removeProduct(Integer.parseInt(itemIdToChangeQuantity));
                }
                if (Integer.parseInt(newQuantity) > 0) {
                    if (Integer.parseInt(newQuantity) >= carDao.get(Integer.parseInt(itemIdToChangeQuantity))) {
                        int quantityDifference = Integer.parseInt(newQuantity) - carDao.get(Integer.parseInt(itemIdToChangeQuantity));

                        for (int i = 1; i <= quantityDifference; i++) {
                            carDao.add(Integer.parseInt(itemIdToChangeQuantity));
                        }
//                    }else{
//                        carDao.removeProduct(Integer.parseInt(itemIdToChangeQuantity));
//                        int quantityDifference = carDao.get(Integer.parseInt(itemIdToChangeQuantity))-Integer.parseInt(newQuantity);
//                        for (int i = 1; i <= quantityDifference; i++) {
//                            carDao.add(Integer.parseInt(itemIdToChangeQuantity));
//                        }
                    }

                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.sendRedirect("/");
    }

}
