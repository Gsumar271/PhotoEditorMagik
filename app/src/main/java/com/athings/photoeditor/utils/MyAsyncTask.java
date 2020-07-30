package com.athings.photoeditor.utils;

import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MyAsyncTask<Params, Progress, Result> {
    private static int[] $SWITCH_TABLE$com$lyrebirdstudio$common_libs$MyAsyncTask$Status = null;
    private static final String LOG_TAG = "MyAsyncTask";
    private static final int MAXIMUM_POOL_SIZE = 10;
    private static final int MESSAGE_POST_CANCEL = 3;
    private static final int MESSAGE_POST_PROGRESS = 2;
    private static final int MESSAGE_POST_RESULT = 1;
    private static final ThreadPoolExecutor sExecutor;
    private static final InternalHandler sHandler;
    private static final ThreadFactory sThreadFactory;
    private static final BlockingQueue<Runnable> sWorkQueue;
    private final FutureTask<Result> mFuture;
    private volatile Status mStatus;
    private final WorkerRunnable<Params, Result> mWorker;
    private static MyAsyncTaskResult result;

    private static class InternalHandler extends Handler {
        private InternalHandler() {
        }

        public void handleMessage(Message msg) {
            result = (MyAsyncTaskResult) msg.obj;
            switch (msg.what) {
                case MyAsyncTask.MESSAGE_POST_RESULT /*1*/:
                    result.mTask.finish(result.mData[0]);
                case MyAsyncTask.MESSAGE_POST_PROGRESS /*2*/:
                    result.mTask.onProgressUpdate(result.mData);
                case MyAsyncTask.MESSAGE_POST_CANCEL /*3*/:
                    result.mTask.onCancelled();
                default:
            }
        }
    }

    private static class MyAsyncTaskResult<Data> {
        final Data[] mData;
        final MyAsyncTask mTask;

        MyAsyncTaskResult(MyAsyncTask task, Data... data) {
            this.mTask = task;
            this.mData = data;
        }
    }

    public enum Status {
        PENDING,
        RUNNING,
        FINISHED
    }

    private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
        Params[] mParams;

        private WorkerRunnable() {
        }
    }

    protected abstract Result doInBackground(Params... paramsArr);

    static int[] $SWITCH_TABLE$com$lyrebirdstudio$common_libs$MyAsyncTask$Status() {
        int[] iArr = $SWITCH_TABLE$com$lyrebirdstudio$common_libs$MyAsyncTask$Status;
        if (iArr == null) {
            iArr = new int[Status.values().length];
            try {
                iArr[Status.FINISHED.ordinal()] = MESSAGE_POST_CANCEL;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Status.PENDING.ordinal()] = MESSAGE_POST_RESULT;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[Status.RUNNING.ordinal()] = MESSAGE_POST_PROGRESS;
            } catch (NoSuchFieldError e3) {
            }
            $SWITCH_TABLE$com$lyrebirdstudio$common_libs$MyAsyncTask$Status = iArr;
        }
        return iArr;
    }

    static {
        sWorkQueue = new LinkedBlockingQueue(MAXIMUM_POOL_SIZE);
        sThreadFactory = new ThreadFactory() {
            AtomicInteger mCount;

            @Override
            public Thread newThread(@NonNull Runnable runnable) {
                this.mCount = new AtomicInteger(MyAsyncTask.MESSAGE_POST_RESULT);
                return new Thread(runnable, "MyAsyncTask #" + this.mCount.getAndIncrement());
            }
        };
        sExecutor = new ThreadPoolExecutor(MESSAGE_POST_RESULT, MAXIMUM_POOL_SIZE, 10, TimeUnit.SECONDS, sWorkQueue, sThreadFactory);
        sHandler = new InternalHandler();
    }

    public MyAsyncTask() {
        this.mStatus = Status.PENDING;
        this.mWorker = new WorkerRunnable<Params, Result>() {
            @Override
            public Result call() throws Exception {
                Process.setThreadPriority(MAXIMUM_POOL_SIZE);
                return doInBackground(mParams);
            }
        };
        this.mFuture = new FutureTask<Result>(mWorker) {
            @Override
            protected void done() {
                Result result = null;
                try {
                    result = get();
                } catch (InterruptedException e) {
                    Log.w(MyAsyncTask.LOG_TAG, e);
                } catch (ExecutionException e2) {
                    throw new RuntimeException("An error occured while executing doInBackground()", e2.getCause());
                } catch (CancellationException e3) {
                    MyAsyncTask.sHandler.obtainMessage(MyAsyncTask.MESSAGE_POST_CANCEL, new MyAsyncTaskResult(MyAsyncTask.this, (Object[]) null)).sendToTarget();
                    return;
                } catch (Throwable t) {
                    RuntimeException runtimeException = new RuntimeException("An error occured while executing doInBackground()", t);
                }
                Object[] objArr = new Object[MyAsyncTask.MESSAGE_POST_RESULT];
                objArr[0] = result;
                sHandler.obtainMessage(MyAsyncTask.MESSAGE_POST_RESULT, new MyAsyncTaskResult(MyAsyncTask.this, objArr)).sendToTarget();
            }
        };
    }

    public final Status getStatus() {
        return this.mStatus;
    }

    protected void onPreExecute() {
    }

    protected void onPostExecute(Result result) {
    }

    protected void onProgressUpdate(Progress... values) {
    }

    protected void onCancelled() {
    }

    public final boolean cancel(boolean mayInterruptIfRunning) {
        return this.mFuture.cancel(mayInterruptIfRunning);
    }

    public final Result get() throws InterruptedException, ExecutionException {
        return this.mFuture.get();
    }

    public final Result get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.mFuture.get(timeout, unit);
    }

    public final MyAsyncTask<Params, Progress, Result> execute(Params... params) {
        if (this.mStatus != Status.PENDING) {
            switch ($SWITCH_TABLE$com$lyrebirdstudio$common_libs$MyAsyncTask$Status()[this.mStatus.ordinal()]) {
                case MESSAGE_POST_PROGRESS /*2*/:
                    throw new IllegalStateException("Cannot execute task: the task is already running.");
                case MESSAGE_POST_CANCEL /*3*/:
                    throw new IllegalStateException("Cannot execute task: the task has already been executed (a task can be executed only once)");
            }
        }
        this.mStatus = Status.RUNNING;
        onPreExecute();
        this.mWorker.mParams = params;
        sExecutor.execute(this.mFuture);
        return this;
    }

    protected final void publishProgress(Progress... values) {
        sHandler.obtainMessage(MESSAGE_POST_PROGRESS, new MyAsyncTaskResult(this, values)).sendToTarget();
    }

    private void finish(Result result) {
        onPostExecute(result);
        this.mStatus = Status.FINISHED;
    }
}
