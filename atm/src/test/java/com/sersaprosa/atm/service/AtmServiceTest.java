package com.sersaprosa.atm.service;

import com.sersaprosa.atm.exception.UserNotFoundException;
import com.sersaprosa.atm.model.Account;
import com.sersaprosa.atm.model.BankUser;
import com.sersaprosa.atm.repository.AccountRepository;
import com.sersaprosa.atm.repository.TransactionRepository;
import com.sersaprosa.atm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtmServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private AtmService atmService;

    @Test
    void getAllUsers() {
        atmService.getAllUsers();
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsersAccount() {
        atmService.getAllUsersAccount();
        verify(accountRepository).findAll();
    }

    @Test
    void getAllTransactions() {
        atmService.getAllTransactions();
        verify(transactionRepository).findAll();
    }

    @Test
    void getHighestAccountBalance() {
        atmService.getHighestAccountBalance();
        verify(accountRepository).findUserWithHighestBalance();
    }

    @Test
    void calculateBankBalance() {
        Account account1 = new Account();
        account1.setBalance(BigDecimal.valueOf(100));

        Account account2 = new Account();
        account2.setBalance(BigDecimal.valueOf(200));

        Account account3 = new Account();
        account3.setBalance(BigDecimal.valueOf(300));

        List<Account> accounts = List.of(account1, account2, account3);
        when(accountRepository.findAll()).thenReturn(accounts);

        BigDecimal bankBalance = atmService.calculateBankBalance();

        assertEquals(BigDecimal.valueOf(600), bankBalance);
    }

    @Test
    void findUserWithMostTransactions() {
        BankUser bankUser = new BankUser(1,"Carlos Alexander", "De Leon Gutierrez","deleongutierrez7@gmail.com","3456");
        when(transactionRepository.getUserIdWithMostTransactions()).thenReturn(1);
        when(userRepository.findBankUserById(1)).thenReturn(Optional.of(bankUser));
        BankUser result = atmService.findUserWithMostTransactions();
        assertEquals(bankUser,result);
    }

    @Test
    void testFindUserWithMostTransactionsThrowsException() {
        when(transactionRepository.getUserIdWithMostTransactions()).thenReturn(null);
        assertThatThrownBy(() -> atmService.findUserWithMostTransactions())
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found!");
    }

    @Test
    void getTransactionsBetweenDate() {
        LocalDateTime start = LocalDateTime.of(2023,2,26,5,0);
        LocalDateTime end = LocalDateTime.of(2023,2,26,10,0);
        atmService.getTransactionsBetweenDate(start,end);
        verify(transactionRepository).getBankTransactionsBetweenDates(start,end);
    }


}