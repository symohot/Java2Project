package com.symohot.classproject.etc;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ConnectionProvider {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
    private static final ConnectionFactory conFac = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
    public static Connection jsmCon() {
        try {
            return conFac.createConnection();
        } catch (JMSException e) {
            throw new RuntimeException("JSM Connection Problem",e);
        }
    }
    public static EntityManager entCon() {
        try {
            return emf.createEntityManager();
        } catch (RuntimeException e) {
            throw new RuntimeException("EntityManager creating Problem",e);
        }

    }
}
