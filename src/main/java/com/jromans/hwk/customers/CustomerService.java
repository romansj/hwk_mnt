package com.jromans.hwk.customers;

import com.jromans.hwk.configuration.mapping.MapStructMapper;
import com.jromans.hwk.customers.db.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository repository;

    public List<CustomerDTO> findAll() {
        var customers = repository.findAll();
        return MapStructMapper.INSTANCE.customersToDTO(customers);
    }

}
