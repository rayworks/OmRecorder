package omrecorder;

import android.os.Process;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by rayworks on 9/22/16.
 */

/* package */ final class Scheduler {
    private Executor exec = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "AudioRecordThread");
            thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
            return thread;
        }
    });

    private Scheduler() {
    }

    private static Scheduler scheduler;

    public synchronized static Scheduler get() {
        if (scheduler == null) {
            scheduler = new Scheduler();
        }
        return scheduler;
    }

    public Executor executor() {
        return exec;
    }
}
