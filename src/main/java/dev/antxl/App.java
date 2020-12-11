package dev.antxl;

import dev.antxl.retry.ListenerRegistry;
import dev.antxl.retry.Retry;
import dev.antxl.retry.RetryActionListener;
import dev.antxl.retry.RetryEvent;

import java.util.Scanner;

public class App 
{
    public static void main( String[] args )
    {
        ListenerRegistry.register("parseInput", new RetryActionListener() {
            @Override
            public void onRetry(RetryEvent event) {
                System.out.println(event.getCause().toString());
                System.out.printf("第%d次重试：",event.getCurrentAttempt());
            }
        });
        System.out.println("成功输入："+parseInput());
    }

    @Retry(value = Exception.class,name = "parseInput",maxAttempts = 6)
    public static int parseInput()
    {
        Scanner scanner=new Scanner(System.in);
        return Integer.parseInt(scanner.nextLine());
    }
}
