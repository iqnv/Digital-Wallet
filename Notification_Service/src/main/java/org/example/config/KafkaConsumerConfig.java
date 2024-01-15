package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dto.UserCreatedPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;

@Configuration
public class KafkaConsumerConfig {

    private static Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JavaMailSender javaMailSender;

    @KafkaListener(topics = "USER_CREATED", groupId = "notificationMail")
    public void consumeUserCreatedFromKafka(ConsumerRecord payload) throws IOException {

        LOGGER.info("Getting payload from kafka: {}",payload);
        UserCreatedPayload userCreatedPayload = objectMapper.readValue(payload.value().toString(),UserCreatedPayload.class);
        LOGGER.info("Getting userCreatedPayload from kafka: {}",userCreatedPayload);
        callMailSender(userCreatedPayload);

    }
    private void callMailSender(UserCreatedPayload userCreatedPayload) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("indratpay@gmail.com");
        simpleMailMessage.setSubject("Welcome "+userCreatedPayload.getUserName()+" - At INDRat-Pay!");
        simpleMailMessage.setTo(userCreatedPayload.getUserEmail());
        simpleMailMessage.setText("Hi "+userCreatedPayload.getUserName()+", Welcome in Digital-Wallet world");
        simpleMailMessage.setCc("admin.jbdl46@yopmail.com");
        javaMailSender.send(simpleMailMessage);
    }
}
