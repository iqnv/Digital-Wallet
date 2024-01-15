package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TransactionInitPayload {

    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private Double amount;
    private String requestId;
}
