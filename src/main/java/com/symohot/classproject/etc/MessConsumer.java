package com.symohot.classproject.etc;

import com.symohot.classproject.model.carrier.TranDto;
import com.symohot.classproject.model.service.AccountService;
import jakarta.jms.*;

public class MessConsumer implements MessageListener {
    private final Connection connection;
    private final Session session;
    private MessageConsumer consumer;
    private final AccountService accServ;

    public MessConsumer(Connection connection, AccountService accountService) {
        this.connection = connection;
        try {
            this.session = connection.createSession(true, Session.SESSION_TRANSACTED);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
        this.accServ = accountService;
    }

    public void start() {
        try {
            Destination destination = session.createQueue("BON.REQ");
            consumer = session.createConsumer(destination);
            consumer.setMessageListener(this);
            connection.start();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
    public void stop() {
        try {
            consumer.close();
        } catch (Exception ignored) {}
        try {
            session.close();
        } catch (Exception ignored) {}
        try {
            connection.close();
        } catch (Exception ignored) {}
        System.out.println("JMS Consumer stopped.");
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof MapMessage mapMessage) {
                accServ.tranFromBankB(new TranDto(mapMessage.getString("originAccount"),
                        mapMessage.getString("destinationAccount"),
                        mapMessage.getString("amount")));
                session.commit();
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}