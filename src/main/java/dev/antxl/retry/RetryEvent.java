package dev.antxl.retry;

import java.lang.reflect.Method;

public class RetryEvent{
    private final Throwable cause;
    private final Class<? extends Throwable>[] target;
    private final String name;
    private final Method targetMethod;
    private final int currentAttempt;
    private final int maxAttempts;
    private final long interval;
    private final long increaseWith;
    private final double increaseBy;
    private final long nextInterval;
    private final long maxInterval;
    private final long stopAfter;

    public Throwable getCause() {
        return cause;
    }

    public Class<? extends Throwable>[] getTarget() {
        return target;
    }

    public String getName() {
        return name;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public int getCurrentAttempt() {
        return currentAttempt;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public long getInterval() {
        return interval;
    }

    public long getIncreaseWith() {
        return increaseWith;
    }

    public double getIncreaseBy() {
        return increaseBy;
    }

    public long getNextInterval() {
        return nextInterval;
    }

    public long getMaxInterval() {
        return maxInterval;
    }

    public long getStopAfter() {
        return stopAfter;
    }

    RetryEvent(Retry retry, Throwable cause, Method targetMethod, int currentAttempt, long nextInterval)
    {
        this.cause=cause;
        target=retry.value().clone();
        name=retry.name();
        this.targetMethod=targetMethod;
        this.currentAttempt=currentAttempt;
        maxAttempts=retry.maxAttempts();
        interval=retry.interval();
        increaseWith=retry.increaseWith();
        increaseBy=retry.increaseBy();
        this.nextInterval=nextInterval;
        maxInterval=retry.maxInterval();
        stopAfter=retry.stopAfter();
    }
}
