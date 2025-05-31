package com.sersaprosa.atm.repository;

import com.sersaprosa.atm.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findAccountByBankUserId(UUID id);

    @Query(value = "SELECT * FROM account WHERE balance = (SELECT MAX(balance) FROM account)",nativeQuery = true)
    Account findUserWithHighestBalance();
}
