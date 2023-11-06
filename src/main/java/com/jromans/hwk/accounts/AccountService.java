package com.jromans.hwk.accounts;

import com.jromans.hwk.accounts.db.Account;
import com.jromans.hwk.accounts.db.AccountRepository;
import com.jromans.hwk.configuration.mapping.MapStructMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Transactional
@Service
public class AccountService {

    private final AccountRepository repository;

    public List<AccountDTO> findByCustomerId(String clientId) {
        var accounts = repository.findByCustomer_CustId(Long.valueOf(clientId));
        return MapStructMapper.INSTANCE.accountsToDTO(accounts);
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        return repository.findByAccountNumberIgnoreCase(accountNumber);
    }


}
