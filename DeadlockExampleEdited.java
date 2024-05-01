package edited;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    @SuppressWarnings("empty-statement")
    private void acquireResourcesAndWork(Lock firstLock, Lock secondLock, Resource firstResource, Resource secondResource, String threadName) {
        Resource oldFirstResource = firstResource;
        try {
            while (!firstLock.tryLock(1, TimeUnit.SECONDS));
            System.out.println(threadName + " locked " + firstResource);
            
            try {
                // Имитация работы с ресурсом
                Thread.sleep(100);
                
                while (((ReentrantLock) secondLock).isLocked()) {
                    firstResource = oldFirstResource;
                    firstLock.unlock();
                    System.out.println("Метод: " + threadName + " ожидает ресурс 2. "
                            + "\nОсвобождение ресурса 1.");
                    while (!firstLock.tryLock(1, TimeUnit.SECONDS));
                    System.out.println(threadName + " locked " + firstResource);
                }
                
                while (!secondLock.tryLock(1, TimeUnit.SECONDS));
                System.out.println(threadName + " locked " + secondResource);
                
                try {
                    // Имитация работы с ресурсом
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    secondLock.unlock();
                    System.out.println(threadName + " unlocked " + secondResource);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                firstLock.unlock();
                System.out.println(threadName + " unlocked " + firstResource);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(DeadlockExample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        DeadlockExampleEdited example = new DeadlockExampleEdited();
        example.execute();
    }
}
