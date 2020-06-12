package com.codecool.shop.controller;

import com.codecool.shop.dao.*;

import com.codecool.shop.dao.implementation.*;
import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.model.Product;

import com.codecool.shop.model.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import com.codecool.shop.model.ProductCategory;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@WebServlet(urlPatterns = {"/registration"})
public class RegistrationController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());
        engine.process("registration.html", context, resp.getWriter());

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDao userDao = null;

        try {
            userDao = UserDaoJDBC.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String firstName = req.getParameter("first_name");
        String lastName = req.getParameter("last_name");
        String username = req.getParameter("username");
        String email = req.getParameter("email");
//        String phone = req.getParameter("phone_number");
        String password = req.getParameter("password");
//        String billingAddress = req.getParameter("billing_address");
//        String shippingAddress = req.getParameter("shipping_address");

        User user =new User(username, password, firstName, lastName, email);


        try {
            userDao.add(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("New user added");

        sendEmail(email);

        resp.sendRedirect("/login");

    }

    private void sendEmail(String email){

        String toEmail = email; //client Email
        String subjectEmail = "Welcome at Codecool Shop";
        String messageEmail =  "We are very happy to have you here! Happy shopping!";
        String userGmail="codecoolbucurestitest@gmail.com"; //server Email
        String pswGmail = "strlumina";
        String host = "smtp.gmail.com";


        Properties props = new Properties();

        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props,new javax.mail.Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(userGmail,pswGmail);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(userGmail));
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(toEmail));
            message.setSubject(subjectEmail);
            message.setText(messageEmail);

            Transport.send(message);

            System.out.println("E-mail sent successfully");


        }catch (Exception e){
            e.printStackTrace();
        }

    }

}