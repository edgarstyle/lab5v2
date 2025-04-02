package org.example.exception;

/**
 * exception class показывает, что сценарий содержит рекурсию
 */
public class RecursiveScriptException extends RuntimeException {
    public RecursiveScriptException(String message) {
        super(message);
    }
}