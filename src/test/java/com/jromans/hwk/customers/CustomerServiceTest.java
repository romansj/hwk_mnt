package com.jromans.hwk.customers;

import com.jromans.hwk.BaseIntegrationTest;
import com.jromans.hwk.customers.db.Customer;
import com.jromans.hwk.customers.db.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ActiveProfiles("itest-db")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class CustomerServiceTest extends BaseIntegrationTest {


    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void findAll() {
        customerRepository.save(new Customer("John", "111111-00000"));
        customerRepository.save(new Customer("Jane", "211111-00000"));
        customerRepository.save(new Customer("Jack", "311111-00000"));
        customerRepository.save(new Customer("Jill", "411111-00000"));

        var customers = customerService.findAll();
        assertEquals(4, customers.size());
    }
}