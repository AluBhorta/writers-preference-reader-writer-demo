
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        /*input variables*/
        int numOfWriters = 5;
        int numOfReaders = 20;
        int sleepDurationInSecs = 60;
        int writeIntervalInSecs = 5;
        int readIntervalInSecs = 2;
        String sharedFileName = "SharedFile.txt";


        RWMonitor rwMonitor = new RWMonitor(sharedFileName);
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = 0; i < numOfReaders; i++) {
            executorService.submit(new Reader(rwMonitor, readIntervalInSecs));
        }
        for (int i = 0; i < numOfWriters; i++) {
            executorService.submit(new Writer(rwMonitor, writeIntervalInSecs));
        }

        try {
            Thread.sleep(sleepDurationInSecs * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}
