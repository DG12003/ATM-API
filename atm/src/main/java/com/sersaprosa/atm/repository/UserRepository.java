package com.sersaprosa.atm.repository;

import com.sersaprosa.atm.model.BankUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<BankUser, Integer> {
    Optional<BankUser> findBankUserByFirstName(String firstname);
    Optional<BankUser> findBankUserByFirstNameAndPinNumber(String firstname, String pinNumber);
    Optional<BankUser> findBankUserById(Integer id);
}
