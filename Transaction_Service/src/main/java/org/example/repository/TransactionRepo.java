package org.example.repository;

import org.example.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepo extends JpaRepository<TransactionEntity, Long> {

    TransactionEntity findByTxnId(String txnId);
    Optional<TransactionEntity> findById(Long id);
}
