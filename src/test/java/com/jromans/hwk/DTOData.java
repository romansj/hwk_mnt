package com.jromans.hwk;

import com.jromans.hwk.accounts.AccountDTO;
import com.jromans.hwk.accounts.db.Account;
import com.jromans.hwk.customers.CustomerDTO;
import com.jromans.hwk.transactions.SendRequestDTO;
import com.jromans.hwk.transactions.TransactionResponseDTO;

import java.util.List;

import static com.jromans.hwk.configuration.mapping.MapStructMapper.INSTANCE;


public class DTOData {

    public static SendRequestDTO getRequest() {
        var request = Data.getRequest();
        return INSTANCE.requestToSendDTO(request);
    }

    public static SendRequestDTO getRequest(String accountSrc, String accountDst, double amount) {
        var request = Data.getRequest(accountSrc, accountDst, amount);
        return INSTANCE.requestToSendDTO(request);
    }

    public static List<AccountDTO> getAccounts() {
        var account1 = INSTANCE.accountToDTO(Data.getAccountWithBalances("LV10MINT0012345678001"));
        var account2 = INSTANCE.accountToDTO(Data.getAccountWithBalances("LV10MINT0012345678002"));

        return List.of(account1, account2);
    }

    public static List<CustomerDTO> getCustomers() {
        var customer1 = INSTANCE.customerToDTO(Data.getCustomer().withName("John"));
        var customer2 = INSTANCE.customerToDTO(Data.getCustomer().withName("Jane"));

        return List.of(customer1, customer2);
    }

    public static List<TransactionResponseDTO> getTransactions() {
        Account account1 = Data.getAccountWithBalances("LV10MINT0012345678001");
        Account account2 = Data.getAccountWithBalances("LV10MINT0012345678002");


        TransactionResponseDTO dto12 = INSTANCE.requestToDTO(Data.getRequest(account1, account2));
        TransactionResponseDTO dto21 = INSTANCE.requestToDTO(Data.getRequest(account2, account1));

        return List.of(dto12, dto21);
    }
}
