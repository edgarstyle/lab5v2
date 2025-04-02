package org.example.exception;


/**
 * exception class который показывает, что flat для проверки содержит ошибку
 */
public class FlatValidateException extends RuntimeException {
    public FlatValidateException(String message) {
        super(message);
    }
}
