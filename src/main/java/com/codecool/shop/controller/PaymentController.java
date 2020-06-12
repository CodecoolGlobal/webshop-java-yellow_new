package com.codecool.shop.controller;


import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.dao.CartDao;
import com.codecool.shop.dao.implementation.CartDaoMem;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/cart/payment"})
public class PaymentController extends HttpServlet {

    private List<String> tempList = new ArrayList<>();


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        tempList.add("First Name "+req.getParameter("firstName"));
        tempList.add("Last Name "+req.getParameter("lastName"));
        tempList.add("Phone"+req.getParameter("phone"));
        tempList.add("E-mail"+req.getParameter("email"));
        tempList.add("Billing Address"+req.getParameter("address"));
        tempList.add("Shipping Address"+req.getParameter("address2"));

        resp.sendRedirect("/cart/payment");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CartDao cartDao = CartDaoMem.getInstance();

        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());

        Map<Product, Integer> cartMap = null;
        cartMap = cartDao.getAllDaoMem();


//        HashMap<List,Map> hashMap = new HashMap<List, Map>();
//        hashMap.put(tempList,cartDao.getAll());
//        System.out.println(hashMap);

        int numberOfProducts = 0;
        double sum = 0;

        for(Map.Entry<Product, Integer> entry : cartMap.entrySet()){
            sum += entry.getKey().getPriceDouble() * entry.getValue();
            numberOfProducts += entry.getValue();
        }

        context.setVariable("totalPrice", sum);
        context.setVariable("totalNumberOfItems", numberOfProducts);

        engine.process("paymentPage.html", context, resp.getWriter());

    }
}
