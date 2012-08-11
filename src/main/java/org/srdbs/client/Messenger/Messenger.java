package org.srdbs.client.Messenger;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.Logger;
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
                logger.info("Message received : " + ((TextMessage) message).getText()
                        + " from : " + message.getJMSReplyTo());
            }
            response.setJMSCorrelationID(message.getJMSCorrelationID());
            producer.send(message.getJMSReplyTo(), response);
            logger.info("Send respond : " + response.getText()
                    + " to : " + message.getJMSReplyTo());

        } catch (JMSException e) {
            logger.error("Error occurred while receiving the message : " + e);
        }
    }

    public String handleRequest(String messageText) {

        String msg = "";
        String[] temp;
        String delimiter = ":";
        temp = messageText.split(delimiter);

        switch (temp[0].toLowerCase().trim()) {

            case "init":
                msg = MessageHandler.handleInit();
                break;

            case "status":
                msg = "System is alive.";
                break;

            case "upload":
                if (!(temp.length == 3)) {
                    msg = "Message is not in the format of - upload:[full/sp]:<data>. "
                            + "Message was : " + temp[2] + ":" + temp[3];
                } else {
                    msg = MessageHandler.handleUpload(temp[1], temp[2]);
                }
                break;

            case "delete":
                msg = MessageHandler.handleDelete(Integer.valueOf(temp[1]));
                break;

            default:
                msg = "Undefined request.";
        }

        return msg;
    }

}
