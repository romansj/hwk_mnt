package com.jromans.hwk.customers.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    List<Customer> findByNameIgnoreCase(String name);

}
