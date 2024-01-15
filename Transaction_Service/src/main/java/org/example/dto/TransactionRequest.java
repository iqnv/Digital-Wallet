package org.example.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TransactionRequest {

    private Long fromUserId;
    private Long toUserId;
    private Double amount;

}
