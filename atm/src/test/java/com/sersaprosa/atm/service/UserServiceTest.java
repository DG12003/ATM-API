package com.sersaprosa.atm.service;

import com.sersaprosa.atm.dto.AuthenticatedUserDto;
import com.sersaprosa.atm.exception.LowBalanceException;
import com.sersaprosa.atm.exception.UserAlreadyExistsException;
import com.sersaprosa.atm.exception.UserNotFoundException;
import com.sersaprosa.atm.exception.UserTransactionNotFoundException;
import com.sersaprosa.atm.model.Account;
import com.sersaprosa.atm.model.BankUser;
import com.sersaprosa.atm.model.Transaction;
import com.sersaprosa.atm.model.TransactionType;
import com.sersaprosa.atm.repository.AccountRepository;
import com.sersaprosa.atm.repository.TransactionRepository;
import com.sersaprosa.atm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Spy
    private UserRepository userRepository;
    @Spy
    private AccountRepository accountRepository;
    @Spy
    private TransactionRepository transactionRepository;
    private AuthenticatedUserDto userDto;
    private BankUser bankUser1;
    private Account account1;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        userDto = new AuthenticatedUserDto();
        userDto.setFirstName("Carlos Alexander");
        userDto.setSecondName("De Leon Gutierrez");
        userDto.setEmail("deleongutierrez7@gmail.com");
        userDto.setPinNumber("3456");

        bankUser1 = new BankUser();
        bankUser1.setFirstName("Carlos Alexander");
        bankUser1.setSecondName("De Leon Gutierrez");
        bankUser1.setEmail("deleongutierrez7@gmail.com");
        bankUser1.setPinNumber("3456");

        account1 = new Account();
        account1.setBankUser(bankUser1);
        account1.setBalance(BigDecimal.valueOf(250));
        account1.setTransactions(List.of(new Transaction()));

        BankUser bankUser2 = new BankUser();
        bankUser2.setFirstName("Daniel Alberto");
        bankUser2.setSecondName("Martinez Hernandez");
        bankUser2.setEmail("martinezhernandez@gmail.com");
        bankUser2.setPinNumber("6789");

        transaction = new Transaction();
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setValue(BigDecimal.valueOf(100));
        transaction.setTransactionType(TransactionType.DEPOSIT);

        userRepository.saveAll(List.of(bankUser1, bankUser2));
    }

    @Test
    void registerUser() {
        Account account = new Account();
        account.setBankUser(bankUser1);

        when(userRepository.findBankUserByFirstName(userDto.getFirstName())).thenReturn(Optional.empty());

        userService.registerUser(userDto);

        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountArgumentCaptor.capture());
        Account capturedAccount = accountArgumentCaptor.getValue();
        assertEquals(capturedAccount,account);

        ArgumentCaptor<BankUser> bankUserArgumentCaptor = ArgumentCaptor.forClass(BankUser.class);
        verify(userRepository).save(bankUserArgumentCaptor.capture());
        BankUser capturedBankUser = bankUserArgumentCaptor.getValue();
        assertEquals(capturedBankUser,bankUser1);
    }

    @Test
    void registerUserThrowsException() {
        when(userRepository.findBankUserByFirstName("Carlos Alexander"))
                .thenReturn(Optional.ofNullable(bankUser1));
        assertThatThrownBy(() -> userService.registerUser(userDto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User already exists!");
    }

    @Test
    void loginUser() {
        when(userRepository.findBankUserByFirstNameAndPinNumber("Carlos Alexander", "3456"))
                .thenReturn(Optional.of(bankUser1));
        BankUser result = userService.loginUser(userDto);
        assertEquals(bankUser1,result);
    }

    @Test
    void loginUserThrowsException() {
        when(userRepository.findBankUserByFirstNameAndPinNumber("Carlos Alexander", "2312"))
                .thenReturn(null);
        assertThatThrownBy(() -> userService.loginUser(userDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Bad credentials!");
    }

    @Test
    void getUserDetails() {
        when(accountRepository.findAccountByBankUserId(1)).thenReturn(Optional.of(account1));
        Account result = userService.getUserDetails(1);
        assertEquals(account1,result);
    }

    @Test
    void getUserDetailsThrowsException() {
        when(accountRepository.findAccountByBankUserId(10)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserDetails(10))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found!");
    }

    @Test
    void createDepositTransaction() {
        Account account = new Account();
        account.setTransactions(new ArrayList<>());
        when(accountRepository.findAccountByBankUserId(1)).thenReturn(Optional.of(account));
        userService.createDepositTransaction(1,BigDecimal.valueOf(100));
        account.getTransactions().add(transaction);
        account.setBalance(account.getBalance().add(BigDecimal.valueOf(100)));

        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountArgumentCaptor.capture());
        Account capturedAccount = accountArgumentCaptor.getValue();
        assertEquals(capturedAccount,account);
    }

    @Test
    void createDepositTransactionThrowsException() {
        when(accountRepository.findAccountByBankUserId(10)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.createDepositTransaction(10,BigDecimal.valueOf(100)))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found!");
    }

    @Test
    void createWithdrawTransaction() {
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(200));
        account.setTransactions(new ArrayList<>());
        when(accountRepository.findAccountByBankUserId(1)).thenReturn(Optional.of(account));
        userService.createWithdrawTransaction(1,BigDecimal.valueOf(100));
        transaction.setTransactionType(TransactionType.WITHDRAW);
        account.getTransactions().add(transaction);
        account.setBalance(account.getBalance().subtract(BigDecimal.valueOf(100)));

        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountArgumentCaptor.capture());
        Account capturedAccount = accountArgumentCaptor.getValue();
        assertEquals(capturedAccount,account);
    }

    @Test
    void createWithdrawTransactionThrowsUserNotFoundException() {
        when(accountRepository.findAccountByBankUserId(10)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.createWithdrawTransaction(10,BigDecimal.valueOf(100)))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found!");
    }

    @Test
    void createWithdrawTransactionThrowsLowBalanceException() {
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(10));
        account.setTransactions(new ArrayList<>());
        when(accountRepository.findAccountByBankUserId(1)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> userService.createWithdrawTransaction(1,BigDecimal.valueOf(100)))
                .isInstanceOf(LowBalanceException.class)
                .hasMessage("Insufficient funds!");
    }

    @Test
    void createTransferTransaction() {
        Account senderAccount = new Account();
        senderAccount.setBalance(BigDecimal.valueOf(200));
        senderAccount.setTransactions(new ArrayList<>());
        Account beneficiaryAccount = new Account();
        beneficiaryAccount.setBalance(BigDecimal.valueOf(400));
        beneficiaryAccount.setTransactions(new ArrayList<>());

        when(accountRepository.findAccountByBankUserId(1)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findAccountByBankUserId(2)).thenReturn(Optional.of(beneficiaryAccount));

        Transaction result = userService.createTransferTransaction(1,2,BigDecimal.valueOf(100));

        assertNotNull(result);
        assertEquals(senderAccount.getBalance(), BigDecimal.valueOf(100));
        assertEquals(beneficiaryAccount.getBalance(), BigDecimal.valueOf(500));
    }

    @Test
    void createTransferTransactionThrowsLowBalanceException() {
        Account senderAccount = new Account();
        senderAccount.setBalance(BigDecimal.valueOf(10));
        senderAccount.setTransactions(new ArrayList<>());
        Account beneficiaryAccount = new Account();
        beneficiaryAccount.setBalance(BigDecimal.valueOf(20));
        beneficiaryAccount.setTransactions(new ArrayList<>());

        when(accountRepository.findAccountByBankUserId(1)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findAccountByBankUserId(2)).thenReturn(Optional.of(beneficiaryAccount));

        assertThatThrownBy(() -> userService.createTransferTransaction(1,2,BigDecimal.valueOf(100)))
                .isInstanceOf(LowBalanceException.class)
                .hasMessage("Insufficient funds!");
    }

    @Test
    void realizeTransferTransaction() {
        BigDecimal amount = BigDecimal.valueOf(100);
        Account senderAccount = new Account(1, new BankUser(), BigDecimal.valueOf(500),new ArrayList<>());
        Account beneficiaryAccount = new Account(2, new BankUser(), BigDecimal.valueOf(500),new ArrayList<>());

        Transaction result = userService.realizeTransferTransaction(amount, senderAccount, beneficiaryAccount);

        assertNotNull(result);
        assertEquals(senderAccount.getBalance(), BigDecimal.valueOf(400));
        assertEquals(beneficiaryAccount.getBalance(), BigDecimal.valueOf(600));
        verify(transactionRepository, atLeast(4)).save(any(Transaction.class));
        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    void getBankStatement() {
        Account account = new Account(1, new BankUser(), BigDecimal.valueOf(500),new ArrayList<>());
        LocalDateTime start = LocalDateTime.of(2023,2,26,5,0);
        LocalDateTime end = LocalDateTime.of(2023,2,26,10,0);
        Transaction transacc = new Transaction();
        transaction.setValue(BigDecimal.valueOf(100));
        transaction.setTimestamp(LocalDateTime.of(2023,2,26,6,0));
        List<Transaction> transactions = List.of(transacc);

        when(accountRepository.findAccountByBankUserId(1)).thenReturn(Optional.of(account));
        when(transactionRepository.getUserTransactionsBetweenDates(1,start,end)).thenReturn(transactions);

        List<Transaction> result = userService.getBankStatement(1,start,end);

        assertEquals(transactions,result);
    }

    @Test
    void getBankStatementThrowsUserNotFoundException() {
        when(accountRepository.findAccountByBankUserId(10)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getBankStatement(10,LocalDateTime.now(),LocalDateTime.now()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found!");
    }

    @Test
    void getBankStatementThrowsUserTransactionsNotFoundException() {
        Account account = new Account(1, new BankUser(), BigDecimal.valueOf(500),new ArrayList<>());

        when(accountRepository.findAccountByBankUserId(1)).thenReturn(Optional.of(account));
        when(transactionRepository.getUserTransactionsBetweenDates(1,LocalDateTime.now(),LocalDateTime.now())).thenReturn(new ArrayList<>());

        assertThatThrownBy(() -> userService.getBankStatement(1,LocalDateTime.now(),LocalDateTime.now()))
                .isInstanceOf(UserTransactionNotFoundException.class)
                .hasMessage("No transactions found!");
    }

    @Test
    void createTransaction() {
        transaction = new Transaction();
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setValue(BigDecimal.valueOf(100));
        transaction.setTransactionType(TransactionType.DEPOSIT);

        userService.createTransaction(BigDecimal.valueOf(100),TransactionType.DEPOSIT);

        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionArgumentCaptor.capture());
        Transaction capturedTransaction = transactionArgumentCaptor.getValue();
        assertEquals(capturedTransaction,transaction);
    }

    @Test
    void shotDownUserBankAccount() {
        when(userRepository.findById(1)).thenReturn(Optional.of(bankUser1));
        when(accountRepository.findAccountByBankUserId(1)).thenReturn(Optional.of(account1));
        when(transactionRepository.getTransactionsByAccountId(1)).thenReturn(new ArrayList<>());

        userService.shotDownUserBankAccount("1");

        verify(userRepository,times(1)).delete(any(BankUser.class));
        verify(accountRepository,times(1)).delete(any(Account.class));
        verify(transactionRepository,times(1)).deleteAll(anyIterable());
    }

    @Test
    void shotDownUserBankAccountUserNotFoundException() {
        when(userRepository.findById(10)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.shotDownUserBankAccount("10"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found!");
    }

}