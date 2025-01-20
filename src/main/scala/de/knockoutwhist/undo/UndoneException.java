package de.knockoutwhist.undo;

public class UndoneException extends RuntimeException {
    public UndoneException(String message) {
        super(message);
    }
}
