package com.sersaprosa.atm.controller;

import com.sersaprosa.atm.dto.AccountDto;
import com.sersaprosa.atm.dto.BasicTransactionDto;
import com.sersaprosa.atm.dto.TransactionDto;
import com.sersaprosa.atm.model.Account;
import com.sersaprosa.atm.model.Transaction;
import com.sersaprosa.atm.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
public class UserController {
    private final ModelMapper modelMapper;
    private final UserService userService;

    public UserController(ModelMapper modelMapper,UserService userService) {
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getUserDetails(@PathVariable UUID id) {
        Account account = userService.getUserDetails(UUID.fromString(String.valueOf(id)));

        AccountDto accountDto = modelMapper.map(account, AccountDto.class);

        return new ResponseEntity<>(accountDto, HttpStatus.OK);
    }
    @PatchMapping(path ="/deposit", produces = "application/json")
    public ResponseEntity<BasicTransactionDto> createUserDeposit(
            @RequestParam String id,
            @RequestParam String amount,
            @RequestParam("pinNumber") String pinNumber) {
        Transaction transaction = userService.createDepositTransaction(UUID.fromString(String.valueOf(id)),new BigDecimal(amount));

        BasicTransactionDto basicTransactionDto = modelMapper.map(transaction, BasicTransactionDto.class);

        return new ResponseEntity<>(basicTransactionDto, HttpStatus.CREATED);
    }

    @PatchMapping(path ="/withdraw", produces = "application/json")
    public ResponseEntity<BasicTransactionDto> createUserWithdraw(
            @RequestParam String id,
            @RequestParam String amount,
            @RequestParam("pinNumber") String pinNumber) {
        Transaction transaction = userService.createWithdrawTransaction(UUID.fromString(String.valueOf(id)),new BigDecimal(amount));

        BasicTransactionDto basicTransactionDto = modelMapper.map(transaction, BasicTransactionDto.class);

        return new ResponseEntity<>(basicTransactionDto, HttpStatus.CREATED);
    }

    @PatchMapping("/transfer")
    public ResponseEntity<TransactionDto> createUserTransfer(
            @RequestParam String idAccount1,
            @RequestParam String idAccount2,
            @RequestParam String amount) {
        Transaction transaction = userService.createTransferTransaction(UUID.fromString(String.valueOf(idAccount1)),
                UUID.fromString(String.valueOf(idAccount2)), new BigDecimal(amount));

        TransactionDto transactionDto = modelMapper.map(transaction, TransactionDto.class);

        return new ResponseEntity<>(transactionDto, HttpStatus.CREATED);
    }
    @GetMapping("/statement")
    public ResponseEntity<List<TransactionDto>> getBankStatementFromAPeriod(
            @RequestParam String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<Transaction> transactions = userService.getBankStatement(UUID.fromString(String.valueOf(id)), start.atStartOfDay(),end.atStartOfDay());

        List<TransactionDto> transactionDtoList = transactions.stream().
                map(tr -> modelMapper.map(tr, TransactionDto.class))
                .toList();

        return new ResponseEntity<>(transactionDtoList,HttpStatus.OK);
    }
    @DeleteMapping("/close-account/{id}")
    public ResponseEntity<String> closeBankAccount(@PathVariable UUID id) {
        userService.shotDownUserBankAccount(id);

        return new ResponseEntity<>("Bank account closed",HttpStatus.OK);
    }
}
