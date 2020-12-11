package dev.antxl.retry;

import java.util.EventListener;

public interface RetryActionListener extends EventListener {
    void onRetry(RetryEvent event);
}
