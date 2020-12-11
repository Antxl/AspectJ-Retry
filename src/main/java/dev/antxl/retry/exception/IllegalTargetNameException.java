package dev.antxl.retry.exception;

public class IllegalTargetNameException extends IllegalArgumentException{
    public IllegalTargetNameException(String targetName)
    {
        super("The target name \""+targetName+"\" is invalid for register listener.");
    }
}
