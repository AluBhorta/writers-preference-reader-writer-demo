import java.io.File;
import java.util.concurrent.Semaphore;

public class RWMonitor {
    /*
     * Read Write Monitor
     *
     * Acting primarily as a data structure.
     * */
    public int readCount, writeCount;
    public Semaphore readMutex, writeMutex, readTry, resourceMutex;
    public File sharedFile;

    public RWMonitor(String sharedFileName) {
        readCount = 0;
        writeCount = 0;
        readMutex = new Semaphore(1);
        writeMutex = new Semaphore(1);
        readTry = new Semaphore(1);
        resourceMutex = new Semaphore(1);
        sharedFile = new File(sharedFileName);
    }
}
