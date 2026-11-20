**1. Резервирование объекта недвижимости при ипотечной сделке (synchronized)**

Механизм исключает ситуацию, когда два кредитных инспектора одновременно привязывают одну и ту же квартиру из реестра застройщика к разным ипотечным договорам.
```Java
public class CollateralReservationExample {
    private boolean isApartmentReserved = false;

    public synchronized void reserveApartment(String dealId, String borrower) {
        if (!isApartmentReserved) {
            System.out.println("Сделка " + dealId + ": проверка прав на квартиру для клиента " + borrower + "...");
            isApartmentReserved = true;
            System.out.println("Сделка " + dealId + ": квартира успешно закреплена в залог за " + borrower + ".");
        } else {
            System.out.println("Сделка " + dealId + ": отказ — объект уже зарезервирован по другой ипотеке.");
        }
    }

    public static void main(String[] args) {
        CollateralReservationExample registry = new CollateralReservationExample();

        Thread deal1 = new Thread(() -> registry.reserveApartment("MORT-501", "Петров И.В."));
        Thread deal2 = new Thread(() -> registry.reserveApartment("MORT-502", "Соколова М.А."));

        deal1.start();
        deal2.start();
    }
}

```


**2. Ограничение обращений конвейера к сервису оценки недвижимости (Semaphore)**

Семафор контролирует нагрузку на внешний API оценочной компании (максимум 2 одновременных расчёта стоимости залога), ставя остальные заявки кредитного конвейера в очередь.
```Java
import java.util.concurrent.Semaphore;

public class AppraisalServiceLimitExample {
    private static final Semaphore APPRAISAL_CHANNELS = new Semaphore(2);

    public static void evaluateProperty(String mortgageId) {
        try {
            APPRAISAL_CHANNELS.acquire();
            System.out.println("Ипотека " + mortgageId + ": запрос рыночной оценки объекта...");
            Thread.sleep(1000); // Имитация расчёта стоимости залога
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("Ипотека " + mortgageId + ": отчёт об оценке загружен в конвейер.");
            APPRAISAL_CHANNELS.release();
        }
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 5; i++) {
            String id = "REQ-90" + i;
            new Thread(() -> evaluateProperty(id)).start();
        }
    }
}
```


**3. Реестр риск-коэффициентов для секьюретизации портфеля (ReentrantReadWriteLock)**

Аналитики и модули формирования траншей могут параллельно читать коэффициенты резервирования без взаимной блокировки, но при пересмотре риск-модели запись монопольно блокирует доступ.
```Java
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SecuritizationRiskRegistryExample {
    private double defaultRiskFactor = 0.045; // Коэффициент ожидаемых потерь по пулу
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public void calculateTrancheYield(String trancheName) {
        rwLock.readLock().lock();
        try {
            System.out.println("Расчёт доходности транша " + trancheName + " с риск-фактором: " + defaultRiskFactor);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void updateRiskFactor(double newFactor) {
        rwLock.writeLock().lock();
        try {
            System.out.println("--- Риск-департамент обновил коэффициент потерь до " + newFactor + " ---");
            this.defaultRiskFactor = newFactor;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public static void main(String[] args) {
        SecuritizationRiskRegistryExample registry = new SecuritizationRiskRegistryExample();

        new Thread(() -> registry.calculateTrancheYield("Senior-A")).start();
        new Thread(() -> registry.calculateTrancheYield("Mezzanine-B")).start();
        new Thread(() -> registry.updateRiskFactor(0.052)).start();
        new Thread(() -> registry.calculateTrancheYield("Junior-C")).start();
    }
}
```


**4. Генерация номеров кредитных досье в конвейере (AtomicInteger)**

Потоки приёма заявок из разных каналов (сайт, мобильное приложение, офисы) атомарно формируют сквозную нумерацию входящих кредитных досье без дублей и блокировок.
```Java
import java.util.concurrent.atomic.AtomicInteger;

public class LoanApplicationSequenceExample {
    private static final AtomicInteger DOSSIER_SEQ = new AtomicInteger(50000);

    public static void main(String[] args) throws InterruptedException {
        Runnable registerChannel = () -> {
            for (int i = 0; i < 3; i++) {
                int dossierNumber = DOSSIER_SEQ.incrementAndGet();
                System.out.println(Thread.currentThread().getName() + " зарегистрировал досье №" + dossierNumber);
            }
        };

        Thread mobileChannel = new Thread(registerChannel, "Мобильный-банк");
        Thread branchChannel = new Thread(registerChannel, "Отделение-Москва");

        mobileChannel.start();
        branchChannel.start();

        mobileChannel.join();
        branchChannel.join();

        System.out.println("Последний присвоенный номер досье: " + DOSSIER_SEQ.get());
    }
}
```

**5. Закрытие сделки секьюретизации ипотечного пула (CountDownLatch)**

Координатор выпуска ипотечных ценных бумаг ждёт завершения трёх обязательных этапов (аудит закладных, регистрация SPV-компании и присвоение кредитного рейтинга), прежде чем открыть размещение облигаций.
```Java
import java.util.concurrent.CountDownLatch;

public class SecuritizationClosingExample {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch stagesLeft = new CountDownLatch(3);

        String[] stages = {
            "Независимый аудит ипотечного покрытия",
            "Регистрация спецюрлица (SPV)",
            "Присвоение рейтинга старшему траншу"
        };

        for (String stage : stages) {
            new Thread(() -> {
                try {
                    Thread.sleep(700); // Имитация выполнения этапа сделки
                    System.out.println("Этап завершён: " + stage);
                    stagesLeft.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }

        System.out.println("Координатор сделки: ожидание готовности всех документов...");
        stagesLeft.await();
        System.out.println("Координатор сделки: пул сформирован, выпуск ипотечных облигаций открыт!");
    }
}
```
