package com.jromans.hwk.transactions;

import com.jromans.hwk.accounts.AccountService;
import com.jromans.hwk.accounts.db.Account;
import com.jromans.hwk.configuration.mapping.MapStructMapper;
import com.jromans.hwk.fx.CurrencyConversionService;
import com.jromans.hwk.transactions.db.Request;
import com.jromans.hwk.transactions.db.RequestRepository;
import com.jromans.hwk.transactions.db.Transaction;
import com.jromans.hwk.transactions.validation.TransactionRequestValidator;
import com.jromans.hwk.transactions.validation.TransactionValidator;
import com.jromans.hwk.transactions.validation.exceptions.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.jromans.hwk.shared.constants.TransactionType.CREDIT;
import static com.jromans.hwk.shared.constants.TransactionType.DEBIT;
import static com.jromans.hwk.transactions.db.Transaction.getTransaction;


@Slf4j
@AllArgsConstructor
@Transactional
@Service
public class TransactionService {

    private final RequestRepository requestRepository;
    private final AccountService accountService;
    private final CurrencyConversionService conversionService;
    private final TransactionExecutor transactionExecutor;


    /**
     * Validates money transfer request, stores in database,
     * creates transactions (debit, credit) and validates those, calls TransactionExecutor to execute the transactions
     *
     * @param requestDTO Payment data
     * @return Returns created Payment with any modifications, e.g., filled Id field
     */
    public TransactionResponseDTO newTransaction(@Valid SendRequestDTO requestDTO) throws InsufficientFundsException, IncompatibleCurrenciesException, AccountNotFoundException, MoneyNotGoingAnywhereException, NotConvertedException {
        var request = saveRequest(requestDTO);

        validateCurrencies(request);

        var savedPayment = updateBalances(request);

        return MapStructMapper.INSTANCE.requestToDTO(savedPayment);
    }

    /**
     * Validates transfer request currency and destination account currency are compatible
     * Checks if currency needs to be converted and converts from account base currency to transfer request currency
     */
    private void validateCurrencies(Request request) throws IncompatibleCurrenciesException, InsufficientFundsException, AccountNotFoundException {
        var accountDestination = getAccount(request.getAccountDst());

        TransactionValidator.validateDestinationAndTransactionCurrencyMatch(accountDestination, request); // calling the same method as in X

        convertCurrencyIfNeeded(request);
    }


    @NotNull
    private Request updateBalances(Request request) throws InsufficientFundsException, NotConvertedException, MoneyNotGoingAnywhereException, AccountNotFoundException {
        var accountOrigin = getAccount(request.getAccountSrc());
        var accountDestination = getAccount(request.getAccountDst());

        TransactionValidator.validate(request, accountOrigin, accountDestination); // as in X -- here
        log.info("Updating balances for request {}", request);

        executeTransactions(request);

        return requestRepository.save(request);
    }

    private void executeTransactions(Request request) throws AccountNotFoundException, InsufficientFundsException {
        var transactions = createTransactions(request);
        transactionExecutor.executeTransactions(transactions);
    }


    @NotNull
    private Request saveRequest(SendRequestDTO requestDTO) {
        TransactionRequestValidator.validate(requestDTO);

        var requestMapped = MapStructMapper.INSTANCE.dtoToRequest(requestDTO);
        return requestRepository.save(requestMapped);
    }


    /**
     * Convert main currency to transfer request currency
     *
     * @param request
     * @throws InsufficientFundsException If there aren't enough funds in main currency of the account
     */
    private void convertCurrencyIfNeeded(Request request) throws InsufficientFundsException, AccountNotFoundException {
        var accountOrigin = getAccount(request.getAccountSrc());

        var hasRequestedCurrency = TransactionValidator.validateAccountHasRequestedCurrency(accountOrigin, request);
        if (hasRequestedCurrency) return;

        log.info("Account does not have enough requested currency");

        convert(request, accountOrigin);
    }

    private void convert(Request request, Account accountOrigin) throws AccountNotFoundException, InsufficientFundsException {
        var conversionData = getConversionRequest(request);
        var fxRequests = getFxRequests(request, conversionData);

        var transactions = getTransactionPair(fxRequests, accountOrigin);
        transactionExecutor.executeTransactions(transactions);
    }

    @NotNull
    private Pair<Transaction, Transaction> getTransactionPair(Pair<Request, Request> fxRequests, Account accountOrigin) {
        var debitTrx = getTransaction(fxRequests.getLeft(), accountOrigin, DEBIT);
        var creditTrx = getTransaction(fxRequests.getRight(), accountOrigin, CREDIT);

        return Pair.of(debitTrx, creditTrx);
    }

    private Pair<Transaction, Transaction> createTransactions(Request request) throws AccountNotFoundException {
        var accountSrcId = request.getAccountSrc();
        var accountDstId = request.getAccountDst();

        var accountSrc = getAccount(accountSrcId);
        var accountDst = getAccount(accountDstId);


        var debit = getTransaction(request, accountSrc, DEBIT);
        var credit = getTransaction(request, accountDst, CREDIT);
        log.info("Created transactions,\ndebit {},\ncredit {}", debit, credit);

        return Pair.of(debit, credit);
    }

    @NotNull
    private Pair<Request, Request> getFxRequests(Request request, ConversionData conversionData) {
        var fxRequestDebit = requestRepository.save(Request.getFxRequest(request, conversionData.accountCurrency(), conversionData.conversionAmount()));
        var fxRequestCredit = requestRepository.save(Request.getFxRequest(request, conversionData.requestCurrency(), conversionData.requestAmount()));

        return Pair.of(fxRequestDebit, fxRequestCredit);
    }

    @NotNull
    private ConversionData getConversionRequest(Request request) throws AccountNotFoundException {
        var accountOrigin = getAccount(request.getAccountSrc());

        var accountCurrency = accountOrigin.getAccountCurrency();
        var requestCurrency = request.getCurrency();
        var requestAmount = request.getAmount();

        var rateToSrc = conversionService.getRate(requestCurrency, accountCurrency);
        var conversionAmount = rateToSrc.multiply(requestAmount);


        var srcBalance = accountOrigin.getBalanceInAccountCurrency();
        var newBalance = srcBalance.subtract(conversionAmount);

        log.info("Currency conversion needed. rateToSrc {}, conversionAmount {}, srcBalance {}, newBalance {}", rateToSrc, conversionAmount, srcBalance, newBalance);
        return new ConversionData(accountCurrency, requestCurrency, requestAmount, conversionAmount);
    }


    public Page<TransactionResponseDTO> findTransactionsByAccountNumber(String accountNumber, Pageable pageable) {
        var requests = requestRepository.findByAccountSrcIgnoreCase(accountNumber, pageable);
        return requests.map(MapStructMapper.INSTANCE::requestToDTO);
    }

    private Account getAccount(String accountSrc) throws AccountNotFoundException {
        return accountService.findByAccountNumber(accountSrc).orElseThrow(AccountNotFoundException::new);
    }


}
