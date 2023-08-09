package ir.sharif.math.ap2023.hw5;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class MultiThreadCopier {
    private SourceProvider sourceProvider;
    private String dest;
    private long workerCount;
    private final ArrayList<Worker> workers = new ArrayList<>();
    public static final long SAFE_MARGIN = 6;

    public MultiThreadCopier(SourceProvider sourceProvider, String dest, long workerCount) {
        this.dest = dest;
        this.workerCount = workerCount;
        this.sourceProvider = sourceProvider;
    }
    //------------------------------------------------methods----------------------------------------------------------//

    public void start() {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(dest , "rws");
            randomAccessFile.setLength(sourceProvider.size());
            randomAccessFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (long i = 0; i < workerCount; i++) {
            long start = i * (sourceProvider.size() / workerCount);
            long end = (i + 1) * (sourceProvider.size() / workerCount);
            if (i == workerCount - 1){
                end = sourceProvider.size();
            }
            Worker worker = new Worker(i , this , start , end);
            workers.add(worker);
        }
        for (int i = 0; i < workerCount; i++) {
            workers.get(i).start();
        }
    }
    public Worker findLazy(Worker strong) {
        long max = -1;
        Worker lazy = null;
        for (Worker worker : workers) {
            if (worker.getEnd() - worker.getOffset() >= max) {
                max = worker.getEnd() - worker.getOffset();
                lazy = worker;
            }
        }
        return lazy;
    }
    public synchronized void helpEachOther(Worker strong) {
        Worker lazy = findLazy(strong);
        if (lazy == null || lazy.getEnd() - lazy.getOffset() < SAFE_MARGIN) {
            return;
        }
        strong.setEnd(lazy.getEnd());
        long init;
        if ((lazy.getEnd() + lazy.getOffset()) % 2 == 0){
            init = (lazy.getEnd() + lazy.getOffset()) / 2;
        }
        else {
            init = (lazy.getEnd() + lazy.getOffset()) / 2 + 1;
        }
        lazy.setEnd(init);
        strong.setStart(lazy.getEnd());
        strong.setStarting(true);
    }
    //------------------------------------------------getter----------------------------------------------------------//
    public SourceProvider getSourceProvider() {
        return sourceProvider;
    }

    public String getDest() {
        return dest;
    }

    public long getWorkerCount() {
        return workerCount;
    }

    public ArrayList<Worker> getWorkers() {
        return workers;
    }

//------------------------------------------------setter----------------------------------------------------------//

    public void setSourceProvider(SourceProvider sourceProvider) {
        this.sourceProvider = sourceProvider;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public void setWorkerCount(long workerCount) {
        this.workerCount = workerCount;
    }

}
