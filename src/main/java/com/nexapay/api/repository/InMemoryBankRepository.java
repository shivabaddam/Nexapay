package com.nexapay.api.repository;

import com.nexapay.api.model.Account;
import com.nexapay.api.model.Customer;
import com.nexapay.api.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryBankRepository {

    private final AtomicLong customerIdGenerator = new AtomicLong(1000);
    private final AtomicLong accountIdGenerator = new AtomicLong(500000);
    private final AtomicLong transactionIdGenerator = new AtomicLong(1);

    private final Map<Long, Customer> customers = new ConcurrentHashMap<>();
    private final Map<Long, Account> accounts = new ConcurrentHashMap<>();
    private final Map<Long, List<Transaction>> transactionsByAccount = new ConcurrentHashMap<>();

    public long nextCustomerId() {
        return customerIdGenerator.incrementAndGet();
    }

    public long nextAccountId() {
        return accountIdGenerator.incrementAndGet();
    }

    public long nextTransactionId() {
        return transactionIdGenerator.getAndIncrement();
    }

    public Customer saveCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
        return customer;
    }

    public Account saveAccount(Account account) {
        accounts.put(account.getId(), account);
        transactionsByAccount.putIfAbsent(account.getId(), new ArrayList<>());
        return account;
    }

    public void saveTransaction(Transaction transaction) {
        transactionsByAccount.computeIfAbsent(transaction.getAccountId(), key -> new ArrayList<>()).add(transaction);
    }

    public Optional<Customer> findCustomerById(Long id) {
        return Optional.ofNullable(customers.get(id));
    }

    public Optional<Account> findAccountById(Long id) {
        return Optional.ofNullable(accounts.get(id));
    }

    public List<Customer> findAllCustomers() {
        return customers.values().stream().toList();
    }

    public List<Account> findAccountsByCustomerId(Long customerId) {
        return accounts.values().stream()
                .filter(a -> a.getCustomerId().equals(customerId))
                .toList();
    }

    public List<Transaction> findTransactionsByAccountId(Long accountId) {
        return List.copyOf(transactionsByAccount.getOrDefault(accountId, List.of()));
    }
}
