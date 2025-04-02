package org.example.exception;


/**
 * exception класс, который показывает, что файл не может быть прочитан
 */
public class FileReadPermissionException extends RuntimeException {
    public FileReadPermissionException(String message) {
        super(message);
    }
}