import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BankAccount {
    private double balance;

    public BankAccount(double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Начальный баланс не может быть отрицательным");
        }
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма пополнения должна быть положительной");
        }
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма снятия должна быть положительной");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Недостаточно средств на счёте");
        }
        balance -= amount;
    }

    public double getBalance() {
        return balance;
    }
}

public class ValidBankAccountTest {

    @Test
    void testNormalOperationsSucceed() {
        BankAccount account = new BankAccount(1000.0);

        assertDoesNotThrow(() -> {
            account.deposit(500.0);
            account.withdraw(300.0);
        });

        assertEquals(1200.0, account.getBalance());
    }

    @Test
    void testNegativeInitialBalanceThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            new BankAccount(-100.0);
        });
    }

    @Test
    void testNegativeOrZeroAmountsThrow() {
        BankAccount account = new BankAccount(1000.0);

        assertThrows(IllegalArgumentException.class, () -> account.deposit(-200.0));
        assertThrows(IllegalArgumentException.class, () -> account.deposit(0.0));
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(-100.0));
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(0.0));

        // Баланс остался неизменным
        assertEquals(1000.0, account.getBalance());
    }

    @Test
    void testOverdraftThrows() {
        BankAccount account = new BankAccount(1000.0);

        assertThrows(IllegalArgumentException.class, () -> {
            account.withdraw(5000.0);
        });

        // Баланс остался неизменным
        assertEquals(1000.0, account.getBalance());
    }
}
