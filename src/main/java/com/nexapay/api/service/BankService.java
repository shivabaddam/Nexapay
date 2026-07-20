package com.nexapay.api.service;

import com.nexapay.api.dto.AmountRequest;
import com.nexapay.api.dto.CreateCustomerRequest;
import com.nexapay.api.dto.OpenAccountRequest;
import com.nexapay.api.dto.TransferRequest;
import com.nexapay.api.exception.BadRequestException;
import com.nexapay.api.exception.ResourceNotFoundException;
import com.nexapay.api.model.Account;
import com.nexapay.api.model.Customer;
import com.nexapay.api.model.Transaction;
import com.nexapay.api.model.TransactionType;
import com.nexapay.api.repository.InMemoryBankRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class BankService {

    private final InMemoryBankRepository repository;

    public BankService(InMemoryBankRepository repository) {
        this.repository = repository;
    }

    public Customer createCustomer(CreateCustomerRequest request) {
        long id = repository.nextCustomerId();
        Customer customer = new Customer(id, request.fullName().trim(), request.email().trim().toLowerCase(), Instant.now());
        return repository.saveCustomer(customer);
    }

    public List<Customer> listCustomers() {
        return repository.findAllCustomers().stream()
                .sorted(Comparator.comparing(Customer::getId))
                .toList();
    }

    public Customer getCustomer(Long id) {
        return repository.findCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
    }

    public Account openAccount(OpenAccountRequest request) {
        getCustomer(request.customerId());
        String currency = normalizeCurrency(request.currency());
        BigDecimal initialDeposit = normalizeMoney(request.initialDeposit());

        long id = repository.nextAccountId();
        Account account = new Account(id, request.customerId(), request.type(), currency, initialDeposit, Instant.now());
        repository.saveAccount(account);

        if (initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
            recordTransaction(account.getId(), null, TransactionType.DEPOSIT, initialDeposit, "Initial deposit");
        }

        return account;
    }

    public Account getAccount(Long id) {
        return repository.findAccountById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id));
    }

    public List<Account> listAccountsByCustomer(Long customerId) {
        getCustomer(customerId);
        return repository.findAccountsByCustomerId(customerId).stream()
                .sorted(Comparator.comparing(Account::getId))
                .toList();
    }

    public Account deposit(Long accountId, AmountRequest request) {
        Account account = getAccount(accountId);
        BigDecimal amount = normalizeMoney(request.amount());
        synchronized (account) {
            account.credit(amount);
        }
        recordTransaction(accountId, null, TransactionType.DEPOSIT, amount, defaultDescription(request.description(), "Deposit"));
        return account;
    }

    public Account withdraw(Long accountId, AmountRequest request) {
        Account account = getAccount(accountId);
        BigDecimal amount = normalizeMoney(request.amount());

        synchronized (account) {
            ensureSufficientFunds(account, amount);
            account.debit(amount);
        }

        recordTransaction(accountId, null, TransactionType.WITHDRAWAL, amount, defaultDescription(request.description(), "Withdrawal"));
        return account;
    }

    public void transfer(TransferRequest request) {
        if (request.fromAccountId().equals(request.toAccountId())) {
            throw new BadRequestException("Source and destination accounts must be different");
        }

        Account from = getAccount(request.fromAccountId());
        Account to = getAccount(request.toAccountId());
        BigDecimal amount = normalizeMoney(request.amount());

        if (!from.getCurrency().equals(to.getCurrency())) {
            throw new BadRequestException("Cross-currency transfers are not supported in this example");
        }

        Account firstLock = from.getId() < to.getId() ? from : to;
        Account secondLock = from.getId() < to.getId() ? to : from;

        synchronized (firstLock) {
            synchronized (secondLock) {
                ensureSufficientFunds(from, amount);
                from.debit(amount);
                to.credit(amount);
            }
        }

        String baseDescription = defaultDescription(request.description(), "Transfer");
        recordTransaction(from.getId(), to.getId(), TransactionType.TRANSFER_OUT, amount, baseDescription + " to " + to.getId());
        recordTransaction(to.getId(), from.getId(), TransactionType.TRANSFER_IN, amount, baseDescription + " from " + from.getId());
    }

    public List<Transaction> listTransactions(Long accountId) {
        getAccount(accountId);
        return repository.findTransactionsByAccountId(accountId).stream()
                .sorted(Comparator.comparing(Transaction::getCreatedAt))
                .toList();
    }

    private void ensureSufficientFunds(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient funds for account " + account.getId());
        }
    }

    private void recordTransaction(Long accountId,
                                   Long relatedAccountId,
                                   TransactionType type,
                                   BigDecimal amount,
                                   String description) {
        Transaction tx = new Transaction(
                repository.nextTransactionId(),
                accountId,
                relatedAccountId,
                type,
                amount,
                description,
                Instant.now()
        );
        repository.saveTransaction(tx);
    }

    private BigDecimal normalizeMoney(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private String normalizeCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            return "USD";
        }
        return currency.trim().toUpperCase();
    }

    private String defaultDescription(String provided, String defaultValue) {
        if (provided == null || provided.isBlank()) {
            return defaultValue;
        }
        return provided.trim();
    }
}
