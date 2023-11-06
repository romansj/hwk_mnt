package com.jromans.hwk.customers;

import com.jromans.hwk.DTOData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    private static final String CUSTOMER_LIST = "/api/v1/customers/list";

    @MockBean
    private CustomerService service;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        var customers = DTOData.getCustomers();
        given(service.findAll()).willReturn(customers);
    }

    @Test
    void basePathOk() throws Exception {
        mockMvc
                .perform(get("/api/v1/customers/"))
                .andExpect(status().isOk());
    }

    @Test
    void allCustomers() throws Exception {
        mockMvc
                .perform(get(CUSTOMER_LIST, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void allCustomersNoContent() throws Exception {
        given(service.findAll()).willReturn(List.of());

        mockMvc
                .perform(get(CUSTOMER_LIST, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}