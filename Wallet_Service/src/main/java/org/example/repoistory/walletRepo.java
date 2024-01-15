package org.example.repoistory;

import org.example.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface walletRepo extends JpaRepository<Wallet, Long> {

    Wallet findByUserId(Long userId);
}
