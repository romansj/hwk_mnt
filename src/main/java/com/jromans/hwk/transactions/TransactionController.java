package com.jromans.hwk.transactions;

import com.jromans.hwk.transactions.validation.exceptions.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;


@Slf4j
@Validated
@AllArgsConstructor
@RequestMapping("/api/v1/transactions")
@RestController
public class TransactionController {

    private final TransactionService service;


    @GetMapping("/list")
    public ResponseEntity<Page<TransactionResponseDTO>> getAllAccountTransactions(@NotBlank @RequestParam String accountNumber,
                                                                                  @RequestParam(defaultValue = "0") int offset,
                                                                                  @RequestParam(defaultValue = "10") int limit) {

        var transactions = service.findTransactionsByAccountNumber(accountNumber, PageRequest.of(offset, limit));
        if (isEmpty(transactions)) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<TransactionResponseDTO> newTransaction(@Valid @RequestBody SendRequestDTO request) throws MoneyNotGoingAnywhereException, NotConvertedException, InsufficientFundsException, IncompatibleCurrenciesException, AccountNotFoundException {
        var savedTransaction = service.newTransaction(request);
        return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<?> getDefault() {
        return ResponseEntity.ok().build();
    }

}
