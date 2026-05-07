package com.symohot.classproject.controller;

import com.symohot.classproject.model.carrier.RegDto;
import com.symohot.classproject.model.service.AccountService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "createAccountServlet", value = "/CreateAccountServlet")
public class CreateAccountServlet extends HttpServlet {
    private static final AccountService accServ = new AccountService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        HttpSession session = req.getSession();
        try {
            accServ.saveInDb(new RegDto(req.getParameter("accountNumber"),
                    req.getParameter("firstName"),
                    req.getParameter("lastName"),
                    req.getParameter("amount"),
                    req.getParameter("status")));
            session.setAttribute("messageC", "Registration Successful");
        } catch (RuntimeException e) {
            session.setAttribute("messageC", e.getMessage());
        } finally {
            resp.sendRedirect("index.jsp");
        }
    }
}
