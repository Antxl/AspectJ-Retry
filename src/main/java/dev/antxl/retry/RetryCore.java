package dev.antxl.retry;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class RetryCore {
    @Around("@annotation(Retry)&&execution(* *(..))")
    public Object doRetry(ProceedingJoinPoint joinPoint)throws Throwable
    {
        Method target=((MethodSignature)joinPoint.getSignature()).getMethod();
        Retry retry=target.getAnnotation(Retry.class);
        int currentAttempt=0;
        long nextInterval=retry.interval(),firstRetryTimeStamp=0;
        boolean ignoreTimes=retry.maxAttempts()==0;
        boolean ignoreTakeUp=retry.stopAfter()==0;
        boolean shouldStop;
        RetryActionListener actionListener=ListenerRegistry.get(retry.name());
        while (true){
            try {
                return joinPoint.proceed();
            }catch (Throwable cause){
                if (!ignoreTimes&&currentAttempt>=retry.maxAttempts())
                    throw cause;
                if (!ignoreTakeUp){
                    if (currentAttempt==0)
                        firstRetryTimeStamp=System.currentTimeMillis();
                    else {
                        shouldStop = System.currentTimeMillis() - firstRetryTimeStamp >= retry.stopAfter();
                        if (shouldStop)
                            throw cause;
                    }
                }
                if (nextInterval>0)
                    Thread.sleep(nextInterval);
                currentAttempt++;
                if (retry.increaseBy()>1)
                    nextInterval=Math.round(nextInterval*retry.increaseBy());
                else if (retry.increaseWith()>0)
                    nextInterval+=retry.increaseWith();
                if (retry.maxInterval()<=nextInterval)
                    nextInterval=retry.maxInterval();
                if (actionListener!=null)
                    actionListener.onRetry(new RetryEvent(retry,cause,target,currentAttempt,nextInterval));
            }
        }
    }
}
