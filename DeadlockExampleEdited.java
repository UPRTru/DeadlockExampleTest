package edited;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockExampleEdited {

    private static class Resource {
        // Ресурсы
    }

    private final Resource resourceA = new Resource();
    private final Resource resourceB = new Resource();
    private final Lock lockA = new ReentrantLock();
    private final Lock lockB = new ReentrantLock();

    public void execute() {
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                acquireResourcesAndWork(lockA, lockB, resourceA, resourceB, "Thread-1");
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                acquireResourcesAndWork(lockB, lockA, resourceB, resourceA, "Thread-2");
            }
        });

        thread1.start();
        thread2.start();
    }

    private synchronized void acquireResourcesAndWork(Lock firstLock, Lock secondLock, Resource firstResource, Resource secondResource, String threadName) {
        Boolean firstLockAcquired = false;
        Boolean secondLockAcquired = false;
        try {
            // Имитация работы с ресурсом
            Thread.sleep(100);
            while (!firstLock.tryLock()) {
                wait();
            }
            firstLockAcquired = true;
            System.out.println(threadName + " locked " + firstResource);
            // Имитация работы с ресурсом
            Thread.sleep(100);
            while (!secondLock.tryLock()) {
                wait();
            }
            secondLockAcquired = true;
            System.out.println(threadName + " locked " + secondResource);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (firstLockAcquired) {
                firstLock.unlock();
                System.out.println(threadName + " unlocked " + firstResource);
            }
            if (secondLockAcquired) {
                secondLock.unlock();
                System.out.println(threadName + " unlocked " + secondResource);
            }
            notify();
        }
    }

    public static void main(String[] args) {
        DeadlockExampleEdited example = new DeadlockExampleEdited();
        example.execute();
    }
}
