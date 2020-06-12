package com.codecool.shop.controller;

import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.dao.CartDao;
import com.codecool.shop.dao.implementation.CartDaoJDBC;
import com.codecool.shop.dao.implementation.CartDaoMem;
import com.codecool.shop.model.Product;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.sql.SQLException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@WebServlet(urlPatterns = "/cart/payment/confirmation")
public class ConfirmationController extends HttpServlet {

    CartDao cartDao = CartDaoMem.getInstance();
    private String statusPayment = "no";


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ccName = req.getParameter("cc-name");
        System.out.println(ccName);
        if (ccName.equals("b")){
            statusPayment ="confirmed";
            try {
                sendEmail();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            statusPayment = "no";
        }
        resp.sendRedirect("/cart/payment/confirmation");
    }

    private void writeJson() throws SQLException {
        JSONObject orderDetails = new JSONObject();

        //Add data to json file
        orderDetails.putAll(cartDao.getAllDaoMem());
        orderDetails.put("statusPayment", statusPayment);

        //Write JSON file
        try (FileWriter file = new FileWriter("orderDetails.json")) {

            file.write(orderDetails.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendEmail() throws SQLException {

        String toEmail = "codecoolbucurestitest@gmail.com"; //client Email
        String subjectEmail = "Your order at Codecool Shop";
        String messageEmail =  "Your order payment is " + statusPayment + "\n"+ cartDao.getAll();
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {



        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());

        Map<Product, Integer> cartMap = null;
        cartMap = cartDao.getAllDaoMem();

        int numberOfProducts = 0;
        double sum = 0;

        for(Map.Entry<Product, Integer> entry : cartMap.entrySet()){
            sum += entry.getKey().getPriceDouble() * entry.getValue();
            numberOfProducts += entry.getValue();
        }

        context.setVariable("cartMap", cartMap);
        context.setVariable("totalPrice", sum);
        context.setVariable("totalNumberOfItems", numberOfProducts);

        CartDao cartDao = null;
        try {
            cartDao = CartDaoJDBC.getInstance();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            writeJson();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        context.setVariable("paymentMessage", statusPayment);


        engine.process("confirmationPage.html", context, resp.getWriter());
        try{
        cartDao.remove(1);}
        catch(SQLException e){
            e.printStackTrace();
        }
    }

}
