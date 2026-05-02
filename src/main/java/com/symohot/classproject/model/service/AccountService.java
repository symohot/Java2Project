package com.symohot.classproject.model.service;

import com.symohot.classproject.etc.ConnectionProvider;
import com.symohot.classproject.model.carrier.RegDto;
import com.symohot.classproject.model.carrier.TranDto;
import com.symohot.classproject.model.entity.Account;
import com.symohot.classproject.model.entity.Status;
import com.symohot.classproject.model.repository.AccountRepository;
import jakarta.jms.*;

import java.util.Optional;

public class AccountService {
    private static final AccountRepository accRepo = new AccountRepository();
    public void saveInDb(RegDto regDto) {
        if (regValidate(regDto)) accRepo.create(makeEntity(regDto),ConnectionProvider.entCon());
//        اضافه کردن چک کردن اکانت
    }
    public void tranFromBankB(TranDto tranDto) {
        accRepo.deposit(tranDto.destinationAccount(),Long.valueOf(tranDto.amount()),ConnectionProvider.entCon());
    }
    public void tranCheck(TranDto tranDto){
        if (tranValidate(tranDto)) toBankB(tranDto);
    }
    private void toBankB(TranDto tranDto) {
        try {
            Connection connection = ConnectionProvider.jsmCon();
            connection.start();
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Destination destination = session.createQueue("BOT.REQ");
            MessageProducer producer = session.createProducer(destination);
            MapMessage msg = session.createMapMessage();
            msg.setString("originAccount", tranDto.originAccount());
            msg.setString("destinationAccount", tranDto.destinationAccount());
            msg.setString("amount", tranDto.amount());
            producer.send(msg);
            session.commit();
            session.close();
            connection.close();
            accRepo.withdraw(tranDto.originAccount(),Long.valueOf(tranDto.amount()),ConnectionProvider.entCon());
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean tranValidate(TranDto tranDto) {
        if (tranDto.originAccount() == null ||
                tranDto.destinationAccount() == null ||
                tranDto.amount() == null) throw new RuntimeException("Fields must not be null");
        else if (tranDto.originAccount().isBlank() ||
                tranDto.destinationAccount().isBlank() ||
                tranDto.amount().isBlank()) throw new RuntimeException("Fields must not be blank");
        else if (!tranDto.originAccount().startsWith("100")) throw new RuntimeException("Origin account must start with '100'");
        else if (!tranDto.destinationAccount().startsWith("200")) throw new RuntimeException("Destination account must start with '200'");
        Optional<Account> optional = Optional.ofNullable(accRepo.read(tranDto.originAccount(), ConnectionProvider.entCon()));
        if (optional.isEmpty()) throw new RuntimeException("OriginAccount not found");
        else if (optional.get().getStatus().equals(Status.BANNED)) throw new RuntimeException("OriginAccount is banned");
        else if (optional.get().getAmount()<Long.parseLong(tranDto.amount())) throw new RuntimeException("OriginAccount amount must be greater than or equal to originAccount amount");
        else return true;
    }
    private boolean regValidate(RegDto regDto) {
        if (regDto.accountNumber() == null ||
                regDto.firstName() == null ||
                regDto.lastName() == null ||
                regDto.amount() == null ||
                regDto.status() == null ) throw new RuntimeException("Fields must not be null");
        else if (regDto.accountNumber().isBlank() || regDto.firstName().isBlank() || regDto.status().isBlank()) throw new RuntimeException("Fields must not be blank");
        //اعتبار سنجی مربوط به شماره حساب و موجودی
        else return true;
    }
    private Account makeEntity(RegDto regDto) {
        return new Account(regDto.accountNumber(),
                regDto.firstName(),
                regDto.lastName(),
                Long.valueOf(regDto.amount()),
                Status.valueOf(regDto.status()));
    }
}