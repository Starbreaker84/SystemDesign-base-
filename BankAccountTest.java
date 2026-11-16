import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class BankAccount {
    private double balance;

    public BankAccount(double initialBalance) {
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public void withdraw(double amount) {
        balance -= amount;
    }

    public double getBalance() {
        return balance;
    }
}

public class BankAccountTest {

    @Test
    void testNormalOperationsDoNotThrow() {
        BankAccount account = new BankAccount(1000.0);

        assertDoesNotThrow(() -> {
            account.deposit(500.0);
            account.withdraw(300.0);
            System.out.println("Баланс после штатных операций: " + account.getBalance());
        });
    }

    @Test
    void testNegativeAmountsDoNotThrow() {
        BankAccount account = new BankAccount(1000.0);

        assertDoesNotThrow(() -> {
            account.deposit(-200.0);
            account.withdraw(-100.0);
            System.out.println("Баланс после отрицательных сумм: " + account.getBalance());
        });
    }

    @Test
    void testOverdraftDoesNotThrow() {
        BankAccount account = new BankAccount(1000.0);

        assertDoesNotThrow(() -> {
            account.withdraw(5000.0);
            System.out.println("Баланс после превышения лимита: " + account.getBalance());
        });
    }
}
