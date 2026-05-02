package com.symohot.classproject.controller;

import com.symohot.classproject.model.carrier.TranDto;
import com.symohot.classproject.model.service.AccountService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "transferServlet", value = "/TransferServlet")
public class TransferServlet extends HttpServlet {
    private static final AccountService accServ = new AccountService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        accServ.tranCheck(new TranDto(req.getParameter("originAccount"),
                req.getParameter("destinationAccount"),
                req.getParameter("amount")));
    }

}
