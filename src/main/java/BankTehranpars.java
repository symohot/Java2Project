import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class BankTehranpars {
    public static void main(String[] args) {
        try {
            ConnectionFactory conFac = new ActiveMQConnectionFactory("tcp://localhost:61616");
            Connection con = conFac.createConnection();
            Session session = con.createSession(true, Session.SESSION_TRANSACTED);
            Destination destination = session.createQueue("BON.REQ");
            MapMessage message = session.createMapMessage();
            MessageProducer producer = session.createProducer(destination);
            for (int i = 1; i < 5; i++) {
                message.setString("originAccount", "200"+i);
                message.setString("destinationAccount", "1001");
                message.setString("amount", String.valueOf(500*i));
                producer.send(message);
            }
            session.commit();
            session.close();
            con.close();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
