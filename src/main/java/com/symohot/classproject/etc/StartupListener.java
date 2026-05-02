package com.symohot.classproject.etc;

import com.symohot.classproject.model.service.AccountService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class StartupListener implements ServletContextListener {
    private MessConsumer messConsumer;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        messConsumer = new MessConsumer(ConnectionProvider.jsmCon(),new AccountService());
        messConsumer.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (messConsumer != null) {
            messConsumer.stop();
        }
    }
}
