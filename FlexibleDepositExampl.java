import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

// 1. Выделяем меняющиеся бизнес-правила в интерфейсы
interface InterestPolicy {
    BigDecimal calculateInterest(BigDecimal balance);
}

interface FeePolicy {
    BigDecimal calculateFee(BigDecimal balance);
}

// 2. Реализации для начисления процентов (полиморфизм)
class SimpleInterestPolicy implements InterestPolicy {
    private final BigDecimal annualRatePercent;

    public SimpleInterestPolicy(BigDecimal annualRatePercent) {
        this.annualRatePercent = annualRatePercent;
    }

    @Override
    public BigDecimal calculateInterest(BigDecimal balance) {
        BigDecimal multiplier = annualRatePercent.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        return balance.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }
}

class VipBonusInterestPolicy implements InterestPolicy {
    private final BigDecimal baseRatePercent;
    private final BigDecimal bonusPercent;

    public VipBonusInterestPolicy(BigDecimal baseRatePercent, BigDecimal bonusPercent) {
        this.baseRatePercent = baseRatePercent;
        this.bonusPercent = bonusPercent;
    }

    @Override
    public BigDecimal calculateInterest(BigDecimal balance) {
        BigDecimal totalRate = baseRatePercent.add(bonusPercent);
        BigDecimal multiplier = totalRate.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        return balance.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }
}

// 3. Реализации для комиссий (полиморфизм)
class FixedFeePolicy implements FeePolicy {
    private final BigDecimal fixedFee;

    public FixedFeePolicy(BigDecimal fixedFee) {
        this.fixedFee = fixedFee;
    }

    @Override
    public BigDecimal calculateFee(BigDecimal balance) {
        return fixedFee;
    }
}

class ZeroFeePolicy implements FeePolicy {
    @Override
    public BigDecimal calculateFee(BigDecimal balance) {
        return BigDecimal.ZERO;
    }
}

// 4. Основной класс вклада: зависимости внедряются через конструктор (Dependency Injection)
final class BankDeposit {
    private final BigDecimal balance;
    private final InterestPolicy interestPolicy;
    private final FeePolicy feePolicy;

    public BankDeposit(BigDecimal balance, InterestPolicy interestPolicy, FeePolicy feePolicy) {
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Баланс не может быть отрицательным");
        }
        this.balance = balance;
        this.interestPolicy = Objects.requireNonNull(interestPolicy);
        this.feePolicy = Objects.requireNonNull(feePolicy);
    }

    public BankDeposit applyMonthlyUpdate() {
        BigDecimal interest = interestPolicy.calculateInterest(balance);
        BigDecimal afterInterest = balance.add(interest);

        BigDecimal fee = feePolicy.calculateFee(afterInterest);
        if (afterInterest.compareTo(fee) < 0) {
            throw new IllegalArgumentException("Недостаточно средств для списания комиссии");
        }

        return new BankDeposit(afterInterest.subtract(fee), interestPolicy, feePolicy);
    }

    public BigDecimal getBalance() {
        return balance;
    }
}

// 5. Меняем тарифы без изменения класса BankDeposit
public class FlexibleDepositExample {
    public static void main(String[] args) {
        BigDecimal initialAmount = new BigDecimal("10000.00");

        // Базовый тариф: 12% годовых и фиксированная комиссия 150 руб.
        BankDeposit standardDeposit = new BankDeposit(
                initialAmount,
                new SimpleInterestPolicy(new BigDecimal("12")),
                new FixedFeePolicy(new BigDecimal("150.00"))
        );
        System.out.println("Базовый тариф после расчёта: " + standardDeposit.applyMonthlyUpdate().getBalance());

        // Премиум-тариф: 12% + 2% бонус и 0 руб. комиссии (ни одной строчки в BankDeposit не меняли)
        BankDeposit vipDeposit = new BankDeposit(
                initialAmount,
                new VipBonusInterestPolicy(new BigDecimal("12"), new BigDecimal("2")),
                new ZeroFeePolicy()
        );
        System.out.println("VIP-тариф после расчёта:     " + vipDeposit.applyMonthlyUpdate().getBalance());
    }
}
