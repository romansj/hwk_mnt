package com.jromans.hwk.accounts;

import com.jromans.hwk.DTOData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    private static final String ACCOUNT_LIST = "/api/v1/accounts/list";
    private static final String PARAM_CLIENT_ID = "clientId";
    private static final String CLIENT_ID = "someFakeId";

    @MockBean
    private AccountService service;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        var accounts = DTOData.getAccounts();
        given(service.findByCustomerId(CLIENT_ID)).willReturn(accounts);
    }

    @Test
    void basePathOk() throws Exception {
        mockMvc
                .perform(get("/api/v1/accounts/"))
                .andExpect(status().isOk());
    }

    @Test
    void noIdentifierNoAccounts() throws Exception {
        mockMvc
                .perform(get(ACCOUNT_LIST, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_CLIENT_ID, "")
                        .content("{}"))
                .andExpect(status().isNoContent());
    }


    @Test
    void identifierReturnsAccounts() throws Exception {
        mockMvc
                .perform(get(ACCOUNT_LIST, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(PARAM_CLIENT_ID, CLIENT_ID))
                .andExpect(status().isOk());
    }
}