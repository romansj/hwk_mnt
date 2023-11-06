package com.jromans.hwk.accounts;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@AllArgsConstructor
@RequestMapping("/api/v1/accounts")
@RestController
public class AccountController {

    private final AccountService service;

    @GetMapping("/")
    public ResponseEntity<?> getDefault() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<AccountDTO>> getAllCustomerAccounts(@RequestParam String clientId) {
        var accounts = service.findByCustomerId(clientId);
        if (accounts.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }
}
