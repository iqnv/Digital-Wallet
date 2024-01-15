package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.dto.TransactionRequest;
import org.example.dto.TxnStatusDto;
import org.example.entity.TransactionStatus;
import org.example.kafka.TransactionalKafkaConsumerConfig;
import org.example.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction-service")
public class TransactionController {

    private static Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);
    @Autowired
    private TransactionService transactionService;
    @GetMapping("/status/{txnId}")
    public ResponseEntity<TxnStatusDto> getStatus(@PathVariable String txnId) {
        return ResponseEntity.ok(transactionService.getStatus(txnId));
    }

    @PostMapping("/makeTransaction")
    public ResponseEntity<String> makeTransaction(@RequestBody TransactionRequest transactionRequest) {
        LOGGER.info("Initiating txn {} ", transactionRequest);
        return ResponseEntity.ok(transactionService.makeTransaction(transactionRequest));
    }
}
