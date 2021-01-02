package dev.antxl.retry;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class RetryCore {
    protected boolean isTargetClass(Retry retry,Throwable cause)
    {
        for (Class<? extends Throwable>target: retry.value()){
            if (target.isAssignableFrom(cause.getClass()))
                return true;
        }
        return false;
    }

    @Around("@annotation(Retry)&&execution(* *(..))")
    public Object doRetry(ProceedingJoinPoint joinPoint)throws Throwable
    {
        Method target=((MethodSignature)joinPoint.getSignature()).getMethod();
        Retry retry=target.getAnnotation(Retry.class);
        int currentAttempt=0;
        long nextInterval=retry.interval(),firstRetryTimeStamp=0;
        boolean ignoreTimes=retry.maxAttempts()<=0;
        boolean ignoreTakeUp=retry.stopAfter()<=0;
        boolean infiniteInterval=retry.maxInterval()<=0;
        RetryActionListener actionListener=ListenerRegistry.get(retry.name());
        while (true){
            try {
                return joinPoint.proceed();
            }catch (Throwable cause){
                if (!isTargetClass(retry,cause))
                    throw cause;
                if (!ignoreTimes&&currentAttempt>=retry.maxAttempts())
                    throw cause;
                if (!ignoreTakeUp){
                    if (currentAttempt==0)
                        firstRetryTimeStamp=System.currentTimeMillis();
                    else if (System.currentTimeMillis()-firstRetryTimeStamp>= retry.stopAfter())
                        throw cause;
                }
                if (nextInterval>0)
                    Thread.sleep(nextInterval);
                currentAttempt++;
                if (retry.increaseBy()>1)
                    nextInterval*=retry.increaseBy();
                else if (retry.increaseWith()>0)
                    nextInterval+=retry.increaseWith();
                if (!infiniteInterval&&retry.maxInterval()<=nextInterval)
                    nextInterval=retry.maxInterval();
                if (actionListener!=null)
                    actionListener.onRetry(new RetryEvent(retry,cause,target,currentAttempt,nextInterval));
            }
        }
    }
}
