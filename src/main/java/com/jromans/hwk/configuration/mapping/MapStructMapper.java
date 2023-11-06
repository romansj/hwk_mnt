package com.jromans.hwk.configuration.mapping;

import com.jromans.hwk.accounts.AccountDTO;
import com.jromans.hwk.accounts.BalanceDTO;
import com.jromans.hwk.accounts.db.Account;
import com.jromans.hwk.accounts.db.Balance;
import com.jromans.hwk.customers.CustomerDTO;
import com.jromans.hwk.customers.db.Customer;
import com.jromans.hwk.transactions.SendRequestDTO;
import com.jromans.hwk.transactions.TransactionResponseDTO;
import com.jromans.hwk.transactions.db.Request;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.ZonedDateTime;
import java.util.List;

// builder disabled due to a bug https://github.com/mapstruct/mapstruct/issues/1556#issuecomment-1011493330, this way @AfterMapping is invoked as expected
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface MapStructMapper {
    MapStructMapper INSTANCE = Mappers.getMapper(MapStructMapper.class);


    @Mapping(target = "custId", source = "custId")
    @Mapping(target = "name", source = "name")
    CustomerDTO customerToDTO(Customer customer);

    @Mapping(target = "customerId", source = "customer.custId")
    AccountDTO accountToDTO(Account account);

    @Mapping(target = "accountNumber", source = "account.accountNumber")
    BalanceDTO balanceToDTO(Balance balance);

    List<CustomerDTO> customersToDTO(List<Customer> customers);

    List<AccountDTO> accountsToDTO(List<Account> accounts);

    TransactionResponseDTO requestToDTO(Request account);

    @Mapping(target = "paymentDate", ignore = true)
    Request dtoToRequest(SendRequestDTO request);

    @AfterMapping
    default void addPaymentDate(@MappingTarget Request request) {
        request.setPaymentDate(ZonedDateTime.now());
    }

    SendRequestDTO requestToSendDTO(Request request);
}
