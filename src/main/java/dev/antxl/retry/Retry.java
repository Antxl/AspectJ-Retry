package dev.antxl.retry;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Retry {
    Class<? extends Throwable> value() default Throwable.class;
    String name() default "";
    int maxAttempts() default 0;
    long interval() default 0;
    long increaseWith() default 0;
    double increaseBy() default 1.0;
    long maxInterval() default 0;
    long stopAfter() default 0;
}
