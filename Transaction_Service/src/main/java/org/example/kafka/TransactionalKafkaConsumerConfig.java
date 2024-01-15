package org.example.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dto.TransactionCompletePayLoad;
import org.example.entity.TransactionEntity;
import org.example.entity.TransactionStatus;
import org.example.repository.TransactionRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;
import java.util.Optional;

@Configuration
public class TransactionalKafkaConsumerConfig {

    private static Logger LOGGER = LoggerFactory.getLogger(TransactionalKafkaConsumerConfig.class);

    private static ObjectMapper objectMapper = new ObjectMapper();


    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static String TRANSACTION_STATUS="transaction_status";

    @Autowired
    private TransactionRepo transactionRepo;

    @KafkaListener(topics = "transaction_status", groupId = "transactionApp")
    public void consumeTransactionFromKafka(ConsumerRecord payload) throws IOException {

        LOGGER.info("Getting payload from kafka: {}",payload);
        TransactionCompletePayLoad transactionCompletePayLoad = objectMapper.readValue(payload.value().toString(), TransactionCompletePayLoad.class);
        LOGGER.info("Getting userCreatedPayload from kafka: {}",transactionCompletePayLoad);
        Optional<TransactionEntity> transactionEntity = transactionRepo.findById(transactionCompletePayLoad.getId());
        if (transactionCompletePayLoad.isStatus()) {
            transactionEntity.get().setStatus(TransactionStatus.SUCCESS.getValue());

        }
        else {
            transactionEntity.get().setStatus(TransactionStatus.FAILED.getValue());
            transactionEntity.get().setReason(transactionCompletePayLoad.getReason());
        }
        transactionRepo.save(transactionEntity.get());
    }


}
