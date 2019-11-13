import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;


public class Application {

    public static void main(String[] args) throws Exception {
        thread(new SimpleProducer(), false);
        thread(new SimpleProducer(), false);
        thread(new SimpleConsumer(), false);
        Thread.sleep(1000);
        thread(new SimpleConsumer(), false);
        thread(new SimpleProducer(), false);
        thread(new SimpleConsumer(), false);
        thread(new SimpleProducer(), false);
        Thread.sleep(1000);
        thread(new SimpleConsumer(), false);
        thread(new SimpleProducer(), false);
        thread(new SimpleConsumer(), false);
        thread(new SimpleConsumer(), false);
        thread(new SimpleProducer(), false);
        thread(new SimpleProducer(), false);
        Thread.sleep(1000);
        thread(new SimpleProducer(), false);
        thread(new SimpleConsumer(), false);
        thread(new SimpleConsumer(), false);
        thread(new SimpleProducer(), false);
        thread(new SimpleConsumer(), false);
        thread(new SimpleProducer(), false);
        thread(new SimpleConsumer(), false);

    }

    public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }

    public static class SimpleProducer implements Runnable {
        public void run() {
            try {
            	//Provided localhost karaf activemq url
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

                // credentials are karaf/karaf
                Connection connection = connectionFactory.createConnection("karaf","karaf");
                connection.start();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                //created queue 
                Destination destination = session.createQueue("ESP");
                MessageProducer producer = session.createProducer(destination);
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                //create a message
                String text = "Hello! From Thread: " + Thread.currentThread().getName() + " : " + this.hashCode();
                TextMessage message = session.createTextMessage(text);
                // Tell the producer to send the message
                System.out.println("Sent message: "+ text);
                producer.send(message);
                session.close();
                connection.close();
            }
            catch (Exception e) {
                System.out.println("Caught: " + e);
                e.printStackTrace();
            }
        }
    }

    public static class SimpleConsumer implements Runnable, ExceptionListener {
        public void run() {
            try {

            	//Provided localhost karaf activemq url
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
                //user name and password are karaf/karaf
                Connection connection = connectionFactory.createConnection("karaf","karaf");
                connection.start();
                connection.setExceptionListener(this);
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                //created queue
                Destination destination = session.createQueue("ESP");
                //created consumer on queue
                MessageConsumer consumer = session.createConsumer(destination);
                //waiting for message
                Message message = consumer.receive(1000);

                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();
                //print received message                
                System.out.println("Received Message in thread " +Thread.currentThread().getName() +" and Text: "+ text);
                consumer.close();
                session.close();
                connection.close();
            } catch (Exception e) {
                System.out.println("Caught Exception: " + e);
                e.printStackTrace();
            }
        }

        public synchronized void onException(JMSException ex) {
            System.out.println("JMS Exception occured");
        }
    }
}