import java.io.*;

public class Reader implements Runnable {
    RWMonitor rwMonitor;
    int readIntervalInSecs;

    public Reader(RWMonitor rwMonitor, int readIntervalInSecs) {
        this.rwMonitor = rwMonitor;
        this.readIntervalInSecs = readIntervalInSecs;
    }

    @Override
    public void run() {
        while (true) {
            try {
                /*ENTRY*/
                rwMonitor.readTry.acquire();
                rwMonitor.readMutex.acquire();
                rwMonitor.readCount++;
                if (rwMonitor.readCount == 1) {
                    rwMonitor.resourceMutex.acquire();
                }
                rwMonitor.readMutex.release();
                rwMonitor.readTry.release();

                /*CS*/
                read();

                /*EXIT*/
                rwMonitor.readMutex.acquire();
                rwMonitor.readCount--;
                if (rwMonitor.readCount == 0) {
                    rwMonitor.resourceMutex.release();
                }
                rwMonitor.readMutex.release();

                Thread.sleep(readIntervalInSecs * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void read() {
        System.out.println("Reading from reader on Thread ID: " + Thread.currentThread().getId());
        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new FileReader(rwMonitor.sharedFile)
            );

            StringBuilder out = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                out.append(line);
            }
            System.out.printf("\nRead output: %s\n%n", out.toString());

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
