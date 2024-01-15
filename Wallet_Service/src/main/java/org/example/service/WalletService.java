package org.example.service;

import jakarta.transaction.Transactional;
import org.example.dto.TransactionInitPayload;
import org.example.entity.Wallet;
import org.example.exception.InsuffiecientBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.repoistory.walletRepo;
@Service
public class WalletService {

    @Autowired
    private walletRepo walletRepofind;


    @Transactional
    public void doTransaction(TransactionInitPayload transactionInitPayload) throws InsuffiecientBalance {
        Wallet fromWallet = walletRepofind.findByUserId(transactionInitPayload.getFromUserId());
        if (fromWallet.getBalance() >= transactionInitPayload.getAmount()) {
            Wallet toWallet = walletRepofind.findByUserId(transactionInitPayload.getToUserId());
            Double fromBalance = fromWallet.getBalance() - transactionInitPayload.getAmount();
            Double toBalance = toWallet.getBalance() + transactionInitPayload.getAmount();
            toWallet.setBalance(toBalance);
            fromWallet.setBalance(fromBalance);
            walletRepofind.save(toWallet);
            walletRepofind.save(fromWallet);
        }
        else {
             throw new InsuffiecientBalance("Low Balance");
        }
    }
}
