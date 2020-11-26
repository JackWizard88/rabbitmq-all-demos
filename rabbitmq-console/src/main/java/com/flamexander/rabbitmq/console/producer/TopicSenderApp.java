package com.flamexander.rabbitmq.console.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class TopicSenderApp {
    private static final String EXCHANGE_NAME = "topicExchanger";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                printHelp();
                String command = reader.readLine();
                if (command.equals("/exit")) {
                    System.exit(0);
                } else if (command.equals("/new")) {
                    System.out.println("please provide topic (ex: programming.java)");
                    String topic = reader.readLine();
                    System.out.println("now type the article:");
                    String message = reader.readLine();
                    channel.basicPublish(EXCHANGE_NAME, topic, null, message.getBytes("UTF-8"));
                    System.out.println(" [x] Sent '" + topic + "'\n" + message );
                } else System.out.println("unknown command");
            }
        }
    }

    public static void printHelp() {
        System.out.println("===================== H E L P =====================");
        System.out.println("/new - to send a new article\n/exit - to close app");
        System.out.println("=======================================================");
    }
}