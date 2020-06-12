package com.codecool.shop.controller;

import com.codecool.shop.dao.*;

import com.codecool.shop.dao.implementation.*;
import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.model.Product;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import com.codecool.shop.model.ProductCategory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/login"})
public class LoginController extends HttpServlet {

//    String message = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());
//        displayMessage(context, engine, resp, message);
        engine.process("login.html", context, resp.getWriter());

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDao userDao = null;
        String passwordDB = null;

        try {
            userDao = UserDaoJDBC.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try {
            passwordDB = userDao.getPasswordByUsername(username);
            System.out.println(passwordDB);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (password.equals(passwordDB)) {

            System.out.println("Successfully logged in");
            HttpSession session = req.getSession();
            System.out.println("Session created");
            session.setAttribute("username", username);
            String sessionUsername = (String)session.getAttribute("username");

            resp.sendRedirect("/");
            System.out.println(sessionUsername);
        } else {
//            message = "Invalid credentials";
            resp.sendRedirect("/login");
        }

    }

//    private void displayMessage(WebContext context, TemplateEngine engine, HttpServletResponse response, String message) throws IOException {
//        try {
//            context.setVariable("message", message);
//        }catch (Exception exc) {
//            System.out.println("error");
//        }
//    }

}
