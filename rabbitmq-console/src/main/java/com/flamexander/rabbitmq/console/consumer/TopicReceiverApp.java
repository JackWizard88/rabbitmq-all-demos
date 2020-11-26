package com.flamexander.rabbitmq.console.consumer;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TopicReceiverApp {
    private static final String EXCHANGE_NAME = "topicExchanger";
    private static Channel channel;
    private static String queueName;

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        queueName = channel.queueDeclare().getQueue();
        System.out.println("My queue name: " + queueName);
        channel.queueBind(queueName, EXCHANGE_NAME, "programming.java");
        channel.queueBind(queueName, EXCHANGE_NAME, "programming.python");

        System.out.println(" [*] Waiting for messages");

        new Thread(() -> {

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                while (true) {
                    String input = reader.readLine();
                    String[] text = input.split(" ");

                    switch (text[0]) {
                        case "/exit" :
                            return;
                        case "/help" :
                             showHelp();
                            break;
                        case "/topics" :
                            showTopics();
                            break;
                        case "/subscribe" :
                            subscribe(text[1]);
                            break;
                        case "/unsubscribe" :
                            unsubscribe(text[1]);
                            break;
                        default:
                            showHelp();
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Incoming topic on your subscription '"  + consumerTag + "'");
            System.out.println(message);
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }

    private static void unsubscribe(String topic) {
        try {
            channel.queueUnbind(queueName, EXCHANGE_NAME, topic);
            System.out.println("[*] Successfully unsubscribed from channel " + topic);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void subscribe(String topic) {
        try {
            channel.queueBind(queueName, EXCHANGE_NAME, topic);
            System.out.println("[*] Successfully subscribed to channel " + topic);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showTopics() {
        System.out.println("===================== T O P I C S =====================");
        System.out.print("available topics are:\n" +
                "1. programming.java\n" +
                "2. programming.phyton\n" +
                "3. programming.c#\n" +
                "4. design.uiux\n" +
                "5. design.html/css\n");
        System.out.println("=======================================================");
    }

    private static void showHelp() {
        System.out.println("===================== H E L P =====================");
        System.out.print("/exit - to exit;\n" +
                "/help - to show help;\n" +
                "/topics - to show available topics;\n" +
                "/subscribe *topic* - to subscribe topic;\n" +
                "/unsubscribe *topic* - to UNsubscribe topic;\n");
        System.out.println("=======================================================");
    }


}
