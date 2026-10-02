
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class BankAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    private String accountNumber;
    private String holderName;
    private BigDecimal balance;
    private List<Transaction> transactions = new ArrayList<>();

    public BankAccount(String accountNumber, String holderName,
                       BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public void deposit(BigDecimal amount) {
        if (amount == null ||
            amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "Enter an amount greater than zero.");
        }

        balance = balance.add(amount);
        transactions.add(new Transaction(
            "Deposit", amount, balance, LocalDateTime.now()));
    }

    public void withdraw(BigDecimal amount) {
        if (amount == null ||
            amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "Enter an amount greater than zero.");
        }

        if (amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException(
                "Insufficient balance.");
        }

        balance = balance.subtract(amount);
        transactions.add(new Transaction(
            "Withdraw", amount, balance, LocalDateTime.now()));
    }

    public abstract String getAccountType();

    public static class Transaction implements Serializable {
        private static final long serialVersionUID = 1L;

        private String type;
        private BigDecimal amount;
        private BigDecimal balanceAfter;
        private LocalDateTime dateTime;

        public Transaction(String type, BigDecimal amount,
                           BigDecimal balanceAfter,
                           LocalDateTime dateTime) {
            this.type = type;
            this.amount = amount;
            this.balanceAfter = balanceAfter;
            this.dateTime = dateTime;
        }

        public String getType() {
            return type;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public BigDecimal getBalanceAfter() {
            return balanceAfter;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }
    }
}