package com.symohot.classproject.etc;

import com.symohot.classproject.model.carrier.TranDto;
import com.symohot.classproject.model.service.AccountService;
import jakarta.jms.*;

public class MessConsumer implements MessageListener {
    private final AccountService accServ = new AccountService();
    private final Connection connection;
    private final Session session;
    private final MessageConsumer consumer;

    public MessConsumer() {
        try {
            this.connection = ConnectionProvider.jmsCon();
            this.session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Destination destination = session.createQueue("BON.REQ");
            this.consumer = session.createConsumer(destination);
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
                accServ.fromBankB(new TranDto(mapMessage.getString("originAccount"),
                        mapMessage.getString("destinationAccount"),
                        mapMessage.getString("amount")));
                session.commit();
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}