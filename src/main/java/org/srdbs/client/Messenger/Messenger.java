package org.srdbs.client.Messenger;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.Logger;
import org.srdbs.client.core.DbConnect;
import org.srdbs.client.core.Global;

import javax.jms.*;

/**
 * Secure and Redundant Data Backup System.
 * User: Thilina Piyasundara
 * Date: 7/12/12
 * Time: 9:34 AM
 * For more details visit : http://www.thilina.org
 */
public class Messenger implements MessageListener {

    private BrokerService broker;
    private Session session;
    private MessageProducer producer;
    private MessageConsumer consumer;

    private String requestQueue = "requests";
    public static Logger logger = Logger.getLogger("systemsLog");
    private String brokerUrl = "tcp://" + Global.serverip + ":" + Global.serverPort;

    public void start() throws Exception {

        logger.info("Starting the broker on : " + brokerUrl);
        createBroker();
        setupConsumer();
    }

    private void createBroker() throws Exception {

        broker = new BrokerService();
        broker.setPersistent(false);
        broker.setUseJmx(false);
        broker.addConnector(brokerUrl);
        broker.start();
    }

    private void setupConsumer() throws JMSException {

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination adminQueue = session.createQueue(requestQueue);

        producer = session.createProducer(null);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        consumer = session.createConsumer(adminQueue);
        consumer.setMessageListener(this);
    }

    public void stop() throws Exception {
        producer.close();
        consumer.close();
        session.close();
        broker.stop();
    }

    public void onMessage(Message message) {
        try {
            TextMessage response = this.session.createTextMessage();
            if (message instanceof TextMessage) {
                TextMessage txtMsg = (TextMessage) message;
                String messageText = txtMsg.getText();
                response.setText(handleRequest(messageText));
            }
            response.setJMSCorrelationID(message.getJMSCorrelationID());
            producer.send(message.getJMSReplyTo(), response);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public String handleRequest(String messageText) {

        String[] temp;
        String delimiter = ":";
        temp = messageText.split(delimiter);

        if (temp[0].equalsIgnoreCase("init")) {
            new DbConnect().initializeDB();
            return "Database initialized successfully.";

        } else if (temp[0].equalsIgnoreCase("data")) {

            return "Receive database details : " + temp[1];
        } else {

            return "Response to '" + messageText + "'";
        }
    }

}
