package com.sersaprosa.atm.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AccountDto {
    private UUID id;
    private BigDecimal balance;
    private List<BasicTransactionDto> transactions;
}
