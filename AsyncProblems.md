**1. Пример ошибки "состояние гонки":**
Итоговое значение счетчика при каждом запуске программы будет разным, потому что сам счетчик инкрементируется не атомарно. Что значение было верным, необходимо операции чтения из памяти, увеличения значения и записи в память производить за один раз. В представленном примере несколько потоков будут читать одно и то же значение и увеличивать его, фактические инкременты будут утеряны, так называемая проблема "потерянного обновления".

Корректный вариант с атомарным инкрементом:
```Java
public class RaceConditionExample {

    private static AtomicInteger counter = 0;

    public static void main(String[] args) {
        int numberOfThreads = 10;
        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    counter.getAndIncrement();
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Final counter value: " + counter);
    }
}
```


**2. Пример ошибки "deadlock":**
Тут классический дэдлок, потоки сначала захватывают каждый свой специфический мьютекс и затем ожидают возможность захватить мьютекс соседнего потока, таким образом ожидание будет бесконечным.

Корректный вариант(например, через попытку захвата потока со сбросом ожидания):
```Java
public class DeadlockExample {

    private static final Lock lock1 = new ReentrantLock();
    private static final Lock lock2 = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> acquireBothLocks("Thread 1", lock1, lock2));
        Thread thread2 = new Thread(() -> acquireBothLocks("Thread 2", lock2, lock1));

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Finished");
    }

    private static void acquireBothLocks(String name, Lock first, Lock second) {
        while (true) {
            if (first.tryLock()) {
                try {
                    System.out.println(name + " acquired first lock");
                    Thread.sleep(50);

                    if (second.tryLock()) {
                        try {
                            System.out.println(name + " acquired second lock");
                            return;
                        } finally {
                            second.unlock();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } finally {
                    first.unlock();
                }
            }
            
            // Небольшая случайная пауза, чтобы потоки не зациклились синхронно (livelock)
            try { 
                Thread.sleep((long) (Math.random() * 10));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); 
                return;
            }
        }
    }
}
```
