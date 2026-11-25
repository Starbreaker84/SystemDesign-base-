**1. ООП-подход (изменяемое состояние)**

Объект Deposit хранит изменяемое поле amount, а методы applyInterest и deductFee напрямую модифицируют текущее состояние экземпляра.

```Java
import java.math.BigDecimal;
import java.math.RoundingMode;

class MutableDeposit {
    private BigDecimal amount;

    public MutableDeposit(BigDecimal initialAmount) {
        if (initialAmount == null || initialAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Сумма вклада не может быть отрицательной");
        }
        this.amount = initialAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }


    public void applyInterest(BigDecimal annualRatePercent) {
        if (annualRatePercent == null || annualRatePercent.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Ставка должна быть положительной");
        }
        BigDecimal multiplier = annualRatePercent.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        BigDecimal interest = amount.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        this.amount = this.amount.add(interest);
    }


    public void deductFee(BigDecimal fee) {
        if (fee == null || fee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Комиссия не может быть отрицательной");
        }
        if (this.amount.compareTo(fee) < 0) {
            throw new IllegalArgumentException("Недостаточно средств для списания комиссии");
        }
        this.amount = this.amount.subtract(fee);
    }
}

public class OopDepositExample {
    public static void main(String[] args) {
        MutableDeposit deposit = new MutableDeposit(new BigDecimal("10000.00"));
        System.out.println("Начальный вклад: " + deposit.getAmount());

        deposit.applyInterest(new BigDecimal("12"));
        System.out.println("После начисления процентов: " + deposit.getAmount());

        deposit.deductFee(new BigDecimal("150.00"));
        System.out.println("После списания комиссии: " + deposit.getAmount());
    }
}

```


**2. Функциональный подход (иммутабельность и чистые функции)**

Класс ImmutableDeposit объявлен как final, его поле amount финализировано, а методы не меняют текущий объект — они возвращают новый экземпляр с обновлённой суммой.
```Java
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

final class ImmutableDeposit {
    private final BigDecimal amount;

    public ImmutableDeposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Сумма вклада не может быть отрицательной");
        }
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }


    public ImmutableDeposit applyInterest(BigDecimal annualRatePercent) {
        if (annualRatePercent == null || annualRatePercent.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Ставка должна быть положительной");
        }
        BigDecimal multiplier = annualRatePercent.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        BigDecimal interest = this.amount.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        return new ImmutableDeposit(this.amount.add(interest));
    }


    public ImmutableDeposit deductFee(BigDecimal fee) {
        if (fee == null || fee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Комиссия не может быть отрицательной");
        }
        if (this.amount.compareTo(fee) < 0) {
            throw new IllegalArgumentException("Недостаточно средств для списания комиссии");
        }
        return new ImmutableDeposit(this.amount.subtract(fee));
    }

    @Override
    public String toString() {
        return "ImmutableDeposit{amount=" + amount + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableDeposit that = (ImmutableDeposit) o;
        return Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}

public class FpDepositExample {
    public static void main(String[] args) {
        ImmutableDeposit initial = new ImmutableDeposit(new BigDecimal("10000.00"));
        System.out.println("Начальный вклад: " + initial);

        ImmutableDeposit afterInterest = initial.applyInterest(new BigDecimal("12"));
        ImmutableDeposit finalDeposit = afterInterest.deductFee(new BigDecimal("150.00"));

        System.out.println("После начисления процентов: " + afterInterest);
        System.out.println("После списания комиссии: " + finalDeposit);
        System.out.println("Исходный объект не изменился: " + initial);
    }
}

```

