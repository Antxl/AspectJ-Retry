package dev.antxl.retry;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
class RetryCore {
    @Around("@annotation(Retry)")
    public Object doRetry(ProceedingJoinPoint joinPoint)throws Throwable
    {
        Method target=((MethodSignature)joinPoint.getSignature()).getMethod();
        Retry retry=target.getAnnotation(Retry.class);
        int currentAttempt=0;
        long nextInterval=retry.interval(),firstRetryTimeStamp=0;
        boolean retryIgnoreTimes=retry.maxAttempts()==0;
        boolean retryIgnoreTakeUp=retry.stopAfter()==0;
        boolean shouldContinue=true;
        RetryActionListener actionListener=ListenerRegistry.get(retry.name());
        while ((retryIgnoreTakeUp||shouldContinue)&&(retryIgnoreTimes||currentAttempt<retry.maxAttempts())){
            try {
                return joinPoint.proceed();
            }catch (Throwable cause){
                if (currentAttempt==0)
                    firstRetryTimeStamp=System.currentTimeMillis();
                else
                    shouldContinue=System.currentTimeMillis()-firstRetryTimeStamp<retry.maxInterval();
                if (nextInterval>0)
                    Thread.sleep(nextInterval);
                if (retry.increaseBy()>1)
                    nextInterval=Math.round(nextInterval*retry.increaseBy());
                else if (retry.increaseWith()>0)
                    nextInterval+=retry.increaseWith();
                if (retry.maxInterval()<=nextInterval)
                    nextInterval=retry.maxInterval();
                currentAttempt++;
                if (actionListener!=null)
                    actionListener.onRetry(new RetryEvent(retry,cause,target,currentAttempt,nextInterval));
            }
        }
        return joinPoint.proceed();
    }
}
