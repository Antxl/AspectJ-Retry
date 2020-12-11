package dev.antxl.retry;

import dev.antxl.retry.exception.IllegalTargetNameException;

import java.util.Hashtable;

public class ListenerRegistry {
    private final static Hashtable<String, RetryActionListener> registry=new Hashtable<>();

    public static void register(String targetName,RetryActionListener listener)
    {
        if (targetName==null||targetName.trim().length()==0)
            throw new IllegalTargetNameException(targetName);
        registry.put(targetName, listener);
    }

    public static <T extends RetryActionListener> T get(String targetName)
    {
        return (T)registry.get(targetName);
    }

    public static void unbind(String targetName)
    {
        registry.remove(targetName);
    }
}
