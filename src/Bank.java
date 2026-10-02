
import java.io.*;
import java.math.BigDecimal;

public class Bank {
    private SavingsAccount account;
    private static final String FILE_NAME =
        System.getProperty("user.home") + File.separator
        + "simple-banking-bankdata.dat";

    public Bank() {
        loadData();
    }

    public SavingsAccount getAccount() {
        return account;
    }

    public SavingsAccount createAccount(String name,
                                        BigDecimal initialBalance) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Please enter the account holder's name.");
        }

        if (initialBalance == null ||
            initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                "Initial balance cannot be negative.");
        }

        if (account != null) {
            throw new IllegalStateException(
                "An account already exists.");
        }

        account = new SavingsAccount(
            "SB1001", name.trim(), initialBalance);

        saveData();
        return account;
    }

    public void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(FILE_NAME))) {
            out.writeObject(account);
        } catch (IOException e) {
            throw new IllegalStateException(
                "Unable to save account data.", e);
        }
    }

    private void loadData() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            account = null;
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(file))) {
            Object data = in.readObject();

            if (data instanceof SavingsAccount) {
                account = (SavingsAccount) data;
            } else {
                throw new IOException("Invalid account data.");
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(
                "Unable to load saved account data.", e);
        }
    }
}