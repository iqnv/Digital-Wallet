package org.example.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dto.TransactionCompletePayLoad;
import org.example.dto.TransactionInitPayload;
import org.example.dto.UserCreatedPayload;
import org.example.entity.Wallet;
import org.example.exception.InsuffiecientBalance;
import org.example.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import java.io.DataOutput;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.CompletableFuture;

import org.example.repoistory.walletRepo;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@Configuration
public class KafkaConsumerConfig {

    private static Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WalletService walletService;
    @Autowired
    private walletRepo walletRepoSave;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static String TRANSACTION_STATUS="transaction_status";

    @KafkaListener(topics = "USER_CREATED", groupId = "walletApp")
    public void consumeUserCreatedFromKafka(ConsumerRecord payload) throws IOException {

        LOGGER.info("Getting payload from kafka: {}",payload);
        UserCreatedPayload userCreatedPayload = objectMapper.readValue(payload.value().toString(),UserCreatedPayload.class);
        LOGGER.info("Getting userCreatedPayload from kafka: {}",userCreatedPayload);
        Wallet wallet = Wallet.builder()
                .userId(userCreatedPayload.getUserId())
                .balance(100.00)
                .build();

        walletRepoSave.save(wallet);
    }

    @KafkaListener(topics = "transaction_service", groupId = "walletApp")
    @Transactional
    public void consumeTransactionCreatedFromKafka(ConsumerRecord payload) throws IOException {

        LOGGER.info("Getting payload from kafka: {}",payload);
        TransactionInitPayload transactionInitPayload = objectMapper.readValue(payload.value().toString(),TransactionInitPayload.class);
        LOGGER.info("Getting userCreatedPayload from kafka: {}",transactionInitPayload.toString());
        TransactionCompletePayLoad transactionCompletePayLoad = new TransactionCompletePayLoad();
        transactionCompletePayLoad.setId(transactionInitPayload.getId());
        try {
            walletService.doTransaction(transactionInitPayload);
            transactionCompletePayLoad.setStatus(true);

        } catch (InsuffiecientBalance e) {
            transactionCompletePayLoad.setStatus(false);
            transactionCompletePayLoad.setReason("Insufficient Balance");
        }
        catch (Exception exception) {
            transactionCompletePayLoad.setStatus(false);
            transactionCompletePayLoad.setReason("Server Error");
        }
        CompletableFuture<SendResult<String, Object>> g = kafkaTemplate.send(TRANSACTION_STATUS, String.valueOf(transactionInitPayload.getFromUserId()), transactionCompletePayLoad);
        try{
            LOGGER.info("Pushed data to kafka {}",transactionCompletePayLoad);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
