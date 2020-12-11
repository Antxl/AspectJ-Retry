package dev.antxl.retry;

import lombok.Getter;

import java.lang.reflect.Method;

@Getter
public class RetryEvent{
    private final Throwable cause;
    private final Class<? extends Throwable> target;
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

    RetryEvent(Retry retry,Throwable cause,Method targetMethod,int currentAttempt,long nextInterval)
    {
        this.cause=cause;
        target=retry.value();
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
