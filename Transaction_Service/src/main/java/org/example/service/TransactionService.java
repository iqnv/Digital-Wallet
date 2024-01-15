package org.example.service;

import org.example.dto.TransactionInitPayload;
import org.example.dto.TransactionRequest;
import org.example.dto.TxnStatusDto;
import org.example.entity.TransactionEntity;
import org.example.entity.TransactionStatus;
import org.example.repository.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static String TRANSACTION_SERVICE="transaction_service";

    public TxnStatusDto getStatus(String txnId) {
        TransactionEntity transactionEntity = transactionRepo.findByTxnId(txnId);
        TxnStatusDto txnStatusDto = new TxnStatusDto(transactionEntity.getStatus(), transactionEntity.getReason());
        return txnStatusDto;

    }

    public String makeTransaction(TransactionRequest transactionRequest) {
        TransactionEntity transactionEntity = TransactionEntity.builder()
                .fromUserId(transactionRequest.getFromUserId())
                .toUserId(transactionRequest.getToUserId())
                .amount(transactionRequest.getAmount())
                .status(TransactionStatus.PENDIND.getValue())
                .txnId(UUID.randomUUID().toString())
                .build();

        transactionRepo.save(transactionEntity);
        TransactionInitPayload transactionInitPayload = TransactionInitPayload.builder()
                .id(transactionEntity.getId())
                .fromUserId(transactionEntity.getFromUserId())
                .toUserId(transactionEntity.getToUserId())
                .amount(transactionEntity.getAmount())
                .build();
        CompletableFuture<SendResult<String, Object>> g = kafkaTemplate.send(TRANSACTION_SERVICE, String.valueOf(transactionEntity.getFromUserId()), transactionInitPayload);
        return transactionEntity.getTxnId();

    }
}
