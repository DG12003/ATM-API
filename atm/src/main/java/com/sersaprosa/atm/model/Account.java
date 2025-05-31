package com.sersaprosa.atm.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Entity
@EqualsAndHashCode
@ToString
@Component
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @OneToOne(cascade = CascadeType.ALL)
    private BankUser bankUser;
    private BigDecimal balance;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private List<Transaction> transactions;

    public Account() {
        this.balance = BigDecimal.ZERO;
    }
}
