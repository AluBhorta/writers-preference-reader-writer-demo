import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer implements Runnable {
    RWMonitor rwMonitor;
    int writeIntervalInSecs;

    public Writer(RWMonitor rwMonitor, int writeIntervalInSecs) {
        this.rwMonitor = rwMonitor;
        this.writeIntervalInSecs = writeIntervalInSecs;
    }

    @Override
    public void run() {
        while (true) {
            try {
                /*ENTRY*/
                rwMonitor.writeMutex.acquire();
                rwMonitor.writeCount++;
                if (rwMonitor.writeCount == 1) {
                    rwMonitor.readTry.acquire();
                }
                rwMonitor.writeMutex.release();
                rwMonitor.resourceMutex.acquire();

                /*CS*/
                write();

                /*EXIT*/
                rwMonitor.resourceMutex.release();
                rwMonitor.writeMutex.acquire();
                rwMonitor.writeCount--;
                if (rwMonitor.writeCount == 0) {
                    rwMonitor.readTry.release();
                }
                rwMonitor.writeMutex.release();

                Thread.sleep(writeIntervalInSecs * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void write() {
        System.out.println("Writing from writer on Thread ID: " + Thread.currentThread().getId());
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new FileWriter(rwMonitor.sharedFile)
            );
            String content = String.format(
                    "Howdy! this is written by Thread ID: %d\n",
                    Thread.currentThread().getId()
            );
            bufferedWriter.write(content);

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
