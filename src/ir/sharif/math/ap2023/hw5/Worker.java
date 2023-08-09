package ir.sharif.math.ap2023.hw5;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Worker extends Thread {
    private long id;
    private long start;
    private long end;
    private long offset;
    private boolean finish = false;
    private boolean starting = false;
    private MultiThreadCopier multiThreadCopier;
    private RandomAccessFile randomAccessFile;

    public Worker(long id, MultiThreadCopier multiThreadCopier , long start , long end) {
        this.start = start;
        this.end = end;
        this.id = id;
        this.multiThreadCopier = multiThreadCopier;
        try {
            this.randomAccessFile = new RandomAccessFile(this.multiThreadCopier.getDest(), "rws");
            this.randomAccessFile.setLength(this.multiThreadCopier.getSourceProvider().size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //------------------------------------------------methods----------------------------------------------------------//
    @Override
    public void run() {
        write();
        finish = true;
        while (true) {
            starting = false;
            multiThreadCopier.helpEachOther(this);
            if (starting){
                write();
            }
            if (helpFinish(multiThreadCopier.getWorkers())) {
                break;
            }
        }
    }
    public void write() {
        SourceReader sourceReader = multiThreadCopier.getSourceProvider().connect(start);
        try {
            randomAccessFile.seek(start);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (offset = start; offset < end; offset++) {
            try {
                randomAccessFile.write(sourceReader.read());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public boolean helpFinish(ArrayList<Worker> workers) {
        for (Worker worker : workers) {
            if (!worker.isFinish()) {
                return false;
            }
        }
        try {
            randomAccessFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    //------------------------------------------------getter----------------------------------------------------------//
    @Override
    public long getId() {
        return id;
    }

    public MultiThreadCopier getMultiThreadCopier() {
        return multiThreadCopier;
    }
    public boolean isFinish() {
        return finish;
    }

    public long getStart() {
        return start;
    }
    public long getEnd() {
        return end;
    }

    public RandomAccessFile getRandomAccessFile() {
        return randomAccessFile;
    }
    public long getOffset() {
        return offset;
    }

    public boolean isStarting() {
        return starting;
    }

    //------------------------------------------------setter----------------------------------------------------------//

    public void setId(long id) {
        this.id = id;
    }

    public void setStarting(boolean starting) {
        this.starting = starting;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void setMultiThreadCopier(MultiThreadCopier multiThreadCopier) {
        this.multiThreadCopier = multiThreadCopier;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public void setRandomAccessFile(RandomAccessFile randomAccessFile) {
        this.randomAccessFile = randomAccessFile;
    }
}
