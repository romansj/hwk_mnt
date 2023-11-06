package com.jromans.hwk.transactions;

import com.jromans.hwk.BaseIntegrationTest;
import com.jromans.hwk.DTOData;
import com.jromans.hwk.Data;
import com.jromans.hwk.accounts.db.Account;
import com.jromans.hwk.accounts.db.AccountRepository;
import com.jromans.hwk.accounts.db.Balance;
import com.jromans.hwk.accounts.db.BalanceRepository;
import com.jromans.hwk.customers.db.Customer;
import com.jromans.hwk.customers.db.CustomerRepository;
import com.jromans.hwk.fx.CurrencyConversionService;
import com.jromans.hwk.shared.constants.MintosCurrency;
import com.jromans.hwk.transactions.db.RequestRepository;
import com.jromans.hwk.transactions.db.TransactionRepository;
import com.jromans.hwk.transactions.validation.exceptions.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.naming.ServiceUnavailableException;
import java.math.BigDecimal;

import static com.jromans.hwk.transactions.TransactionServiceTestDb.CREATED;
import static io.restassured.RestAssured.given;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotEmpty;

@ActiveProfiles("itest-db")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class FullTransactionTest extends BaseIntegrationTest {


    @LocalServerPort
    Integer port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port + "/api/v1";

        clearTestData();
    }

    @Autowired
    TransactionService transactionService;

    @Autowired
    CurrencyConversionService conversionService;

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BalanceRepository balanceRepository;

    @Autowired
    TransactionRepository transactionRepository;

    SendRequestDTO sendMoney(String source, String destination, double amount, MintosCurrency currency) throws InsufficientFundsException, IncompatibleCurrenciesException, ServiceUnavailableException, AccountNotFoundException, MoneyNotGoingAnywhereException, NotConvertedException {
        final var requestDTO = DTOData.getRequest(source, destination, amount).withCurrency(currency);
        transactionService.newTransaction(requestDTO);
        return requestDTO;
    }

    // todo Either use some framework/language or write on my own -- goal: request (however made) succeeds
    class RestRequest {
        static SendRequestDTO sendMoney(String source, String destination, double amount, MintosCurrency currency) {
            var sendRequestDTO = DTOData.getRequest()
                    .withAccountSrc(source)
                    .withAccountDst(destination)
                    .withAmount(BigDecimal.valueOf(amount))
                    .withCurrency(currency);

            given()
                    .contentType(ContentType.JSON).when()
                    .body(sendRequestDTO)
                    .post("/transactions/new")
                    .then()
                    .statusCode(CREATED)
                    .log().body();

            return sendRequestDTO;
        }
    }

    void checkBalance(double balance, String accountNumber, MintosCurrency currency) {
        checkBalance(BigDecimal.valueOf(balance), accountNumber, currency);
    }

    void checkBalance(BigDecimal balance, String accountNumber, MintosCurrency currency) {
        assertThat(balance).isEqualByComparingTo(getBalance(accountNumber, currency));
    }

    void createWithBalance(String name, String accountNumber, double balance, MintosCurrency currency) {
        var customer = getOrCreateCustomer(name);
        var account = getOrCreateAccount(accountNumber, currency, customer);
        assertNotEmpty(accountRepository.findByCustomer_CustId(customer.getCustId()), format("Account not created {0}", accountNumber));

        getOrCreateBalance(account, balance, currency);
        assertThat(valueOf(balance)).isEqualByComparingTo(getBalance(accountNumber, currency));
    }

    private Balance getOrCreateBalance(Account account, double balance, MintosCurrency currency) {
        return balanceRepository.findByAccount_AccountNumberIgnoreCaseAndCurrency(account.getAccountNumber(), currency).orElseGet(() -> {
            return balanceRepository.save(Data.getBalance(account, balance, currency));
        });
    }

    @NotNull BigDecimal getBalance(String accountNumber, MintosCurrency currency) {
        return accountRepository.findByAccountNumberIgnoreCase(accountNumber).map(account -> account.getBalanceInCurrency(currency)).orElse(ZERO);
    }

    @NotNull
    private Account getOrCreateAccount(String accountNumber, MintosCurrency currency, Customer customer) {
        var account = accountRepository.findByAccountNumberIgnoreCase(accountNumber);
        return account.orElseGet(() -> {
            return accountRepository.save(Data.getAccount(accountNumber).withAccountCurrency(currency).withCustomer(customer));
        });
    }

    @NotNull
    private Customer getOrCreateCustomer(String name) {
        var customers = customerRepository.findByNameIgnoreCase(name);
        if (customers.isEmpty()) {
            return customerRepository.save(Data.getCustomer(name));
        }

        return customers.getFirst();
    }

    void clearTestData() {
        balanceRepository.deleteAll();
        accountRepository.deleteAll();
        transactionRepository.deleteAll();
        requestRepository.deleteAll();
        customerRepository.deleteAll();
    }
}
