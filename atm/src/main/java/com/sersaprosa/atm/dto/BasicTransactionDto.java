package com.sersaprosa.atm.dto;

import com.sersaprosa.atm.model.TransactionType;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class BasicTransactionDto {
    private UUID id;
    private LocalDateTime timestamp;
    private BigDecimal value;
    private TransactionType transactionType;
}
