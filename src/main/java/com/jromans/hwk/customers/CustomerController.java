package com.jromans.hwk.customers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@AllArgsConstructor
@RequestMapping("/api/v1/customers")
@RestController
public class CustomerController {

    private final CustomerService service;

    @GetMapping("/list")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        var customers = service.findAll();
        if (customers.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/")
    public ResponseEntity<?> getDefault() {
        return ResponseEntity.ok().build();
    }
}
