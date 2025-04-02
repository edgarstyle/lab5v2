package org.example.exception;


/**
 * exception class показывает, что скрипт содержит ошибку
 */
public class ReadElementFromScriptException extends RuntimeException {
    public ReadElementFromScriptException(String message, Throwable cause) {
        super(message, cause);
    }
}