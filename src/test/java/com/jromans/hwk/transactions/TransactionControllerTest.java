package com.jromans.hwk.transactions;

import com.jromans.hwk.DTOData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    private static final String PARAM_ACCOUNT_ID = "accountNumber";
    private static final String ACCOUNT_ID = "LV10MINT0012345678001";

    @MockBean
    private TransactionService service;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        var transactions = DTOData.getTransactions();
        var pageTransactions = new PageImpl<>(transactions);
        given(service.findTransactionsByAccountNumber(eq(ACCOUNT_ID), any(Pageable.class))).willReturn(pageTransactions);
    }

    @Test
    void basePathOk() throws Exception {
        mockMvc.perform(get("/api/v1/transactions/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void noIdentifierNoTransactions() throws Exception {
        mockMvc
                .perform(get("/api/v1/transactions/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_ACCOUNT_ID, "")
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}