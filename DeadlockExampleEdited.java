package edited;

import java.util.concurrent.locks.ReentrantLock;

public class DeadlockExampleEdited {

    private static class Resource {
        // Ресурсы
    }

    private final Resource resourceA = new Resource();
    private final Resource resourceB = new Resource();
    private final ReentrantLock lockA = new ReentrantLock();
    private final ReentrantLock lockB = new ReentrantLock();

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

    private synchronized void acquireResourcesAndWork(ReentrantLock firstLock, ReentrantLock secondLock, Resource firstResource, Resource secondResource, String threadName) {
        firstLock.lock();
        System.out.println(threadName + " locked " + firstResource);

        try {
            // Имитация работы с ресурсом
            Thread.sleep(100);

            secondLock.lock();
            System.out.println(threadName + " locked " + secondResource);

            try {
                // Имитация работы с ресурсом
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (firstLock.isHeldByCurrentThread()) {
                    firstLock.unlock();
                }
                System.out.println(threadName + " unlocked " + firstResource);
                if (secondLock.isHeldByCurrentThread()) {
                    secondLock.unlock();
                }
                System.out.println(threadName + " unlocked " + secondResource);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        DeadlockExampleEdited example = new DeadlockExampleEdited();
        example.execute();
    }
}
