
import java.math.BigDecimal;

public class SavingsAccount extends BankAccount {

    private static final long serialVersionUID = 1L;

    public SavingsAccount(String accountNumber, String holderName,
                          BigDecimal balance) {
        super(accountNumber, holderName, balance);
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }
}