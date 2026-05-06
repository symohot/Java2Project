package com.symohot.classproject.model.service;

import com.symohot.classproject.etc.ConnectionProvider;
import com.symohot.classproject.model.carrier.RegDto;
import com.symohot.classproject.model.carrier.TranDto;
import com.symohot.classproject.model.entity.Account;
import com.symohot.classproject.model.entity.Status;
import com.symohot.classproject.model.entity.TranMsg;
import com.symohot.classproject.model.repository.AccountRepository;
import jakarta.jms.*;

import java.util.Optional;

public class AccountService {
    private static final AccountRepository accRepo = new AccountRepository();
    private Connection connection;
    private Session session;
    private MessageProducer producer;

    //methods for CreateAccountServlet
    public void saveInDb(RegDto regDto) {
        Optional<Account> optional = Optional.ofNullable(findFromDb(regDto.accountNumber()));
        if (regValidate(regDto) && optional.isEmpty()) accRepo.create(makeRegEntity(regDto),ConnectionProvider.entCon());
        else throw new RuntimeException("Account number exist");
    }
    private boolean regValidate(RegDto regDto) {
        if (regDto.accountNumber() == null ||
                regDto.firstName() == null ||
                regDto.lastName() == null ||
                regDto.amount() == null ||
                regDto.status() == null ) throw new RuntimeException("Fields must not be null");
        else if (regDto.accountNumber().isBlank() ||
                regDto.firstName().isBlank() ||
                regDto.lastName().isBlank() ||
                regDto.status().isBlank()) throw new RuntimeException("Fields must not be blank");
        else if (!regDto.accountNumber().startsWith("100")) throw new RuntimeException("Bank of Narmak accounts start with 100");
        else if (Long.parseLong(regDto.amount())<0) throw new RuntimeException("Amount can't be negative");
        else return true;
    }
    private Account makeRegEntity(RegDto regDto) {
        return new Account(regDto.accountNumber(),
                regDto.firstName(),
                regDto.lastName(),
                Long.valueOf(regDto.amount()),
                Status.valueOf(regDto.status()));
    }

    //methods for transferring money via TransferServlet
    public void tranCheck(TranDto tranDto){
        if (tranValidate(tranDto)){
            if(tranDto.destinationAccount().startsWith("100")) innerTran(tranDto);
            else outerTran(tranDto);
        }
    }
    private boolean tranValidate(TranDto tranDto) {
        if (tranDto.originAccount() == null ||
                tranDto.destinationAccount() == null ||
                tranDto.amount() == null) throw new RuntimeException("Fields must not be null");
        else if (tranDto.originAccount().isBlank() ||
                tranDto.destinationAccount().isBlank() ||
                tranDto.amount().isBlank()) throw new RuntimeException("Fields must not be blank");
        else if (!tranDto.originAccount().startsWith("100")) throw new RuntimeException("This platform is for transferring money between accounts in bank of Narmak OR between accounts in bank of Narmak and bank of Tehranpars, Origin account number must start with '100'");
        else if (!tranDto.destinationAccount().startsWith("100") ||
                !tranDto.destinationAccount().startsWith("200")) throw new RuntimeException("This platform is for accounts in Bank of Tehranpars or Narmak, destination account number must start with '100' or '200'");
        else return true;
    }
    private void innerTran(TranDto tranDto){
        Optional<Account> origin = Optional.ofNullable(findFromDb(tranDto.originAccount()));
        Optional<Account> destin = Optional.ofNullable(findFromDb(tranDto.destinationAccount()));

        if (origin.isEmpty()) throw new RuntimeException("OriginAccount not found");
        else if (origin.get().getStatus().equals(Status.BANNED)) throw new RuntimeException("OriginAccount is banned");
        else if (origin.get().getAmount()>Long.parseLong(tranDto.amount())) throw new RuntimeException("OriginAccount's amount isn't enough");
        else if (destin.isEmpty()) throw new RuntimeException("DestinationAccount not found");
        else if (destin.get().getStatus().equals(Status.BANNED)) throw new RuntimeException("DestinationAccount is banned");
        else {
            TranMsg tranMsg = makeTranEntity(tranDto);
            accRepo.withdraw(tranMsg.getOriginAccount(), tranMsg.getAmount(), ConnectionProvider.entCon());
            accRepo.deposit(tranMsg.getDestinationAccount(), tranMsg.getAmount(), ConnectionProvider.entCon());
        }
    }
    private void outerTran(TranDto tranDto){
        try {
            JMSInit("BOT.REQ");
            MapMessage msg = session.createMapMessage();
            msg.setString("originAccount", tranDto.originAccount());
            msg.setString("destinationAccount", tranDto.destinationAccount());
            msg.setString("amount", tranDto.amount());
            producer.send(msg);
            TranMsg tranMsg = makeTranEntity(tranDto);
            accRepo.withdraw(tranMsg.getOriginAccount(), tranMsg.getAmount(), ConnectionProvider.entCon());
            session.commit();
            JMSDestroy();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    //methods for transferring money via ActiveMQ
    public void fromBankB(TranDto tranDto) {
        try {
            if (msgValidate(tranDto)) {
                TranMsg tranMsg = makeTranEntity(tranDto);
                accRepo.deposit(tranMsg.getDestinationAccount(), tranMsg.getAmount(), ConnectionProvider.entCon());
                msgProducer("Transfer successful");
            }
        } catch (RuntimeException e){
            msgProducer(e.getMessage());
        }

    }
    private boolean msgValidate(TranDto tranDto){
        if (tranDto.originAccount() == null ||
                tranDto.destinationAccount() == null ||
                tranDto.amount() == null) throw new RuntimeException("Message fields are null");
        else if (tranDto.originAccount().isBlank() ||
                tranDto.destinationAccount().isBlank() ||
                tranDto.amount().isBlank()) throw new RuntimeException("Message fields are blank");
        else if (!tranDto.originAccount().startsWith("200")) throw new RuntimeException("Origin account number in bank of Tehranpars must start with '200'");
        else if (!tranDto.destinationAccount().startsWith("100")) throw new RuntimeException("Destination account number in bank of Narmak must start with '100'");
        else if (Long.parseLong(tranDto.amount())<=0) throw new RuntimeException("Amount can't be negative");

        Optional<Account> destin = Optional.ofNullable(findFromDb(tranDto.destinationAccount()));
        if (destin.isEmpty()) throw new RuntimeException("DestinationAccount not found");
        else if (destin.get().getStatus().equals(Status.BANNED)) throw new RuntimeException("DestinationAccount is banned");
        else return true;
    }
    private void msgProducer (String msg){
        try {
            JMSInit("BON.RESP");
            TextMessage textMessage = session.createTextMessage(msg);
            producer.send(textMessage);
            session.commit();
            JMSDestroy();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    //common methods
    private Account findFromDb(String accountNumber){
        return accRepo.read(accountNumber, ConnectionProvider.entCon());
    }
    private void JMSInit(String queue){
        try {
            connection = ConnectionProvider.jmsCon();
            connection.start();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Destination destination = session.createQueue(queue);
            producer = session.createProducer(destination);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
    private void JMSDestroy(){
        try {
            session.close();
            producer.close();
            connection.close();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
    private TranMsg makeTranEntity(TranDto tranDto){
        return new TranMsg(tranDto.originAccount(),tranDto.destinationAccount(),Long.valueOf(tranDto.amount()));
    }
}