package com.sersaprosa.atm.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Component
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private LocalDateTime timestamp;
    @Column(name = "trValue")
    private BigDecimal value;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private UUID fromIdAccount;
    private UUID toIdAccount;
}
