package tqs.backend.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FileStorageExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Storage error";
        FileStorageException exception = new FileStorageException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Storage error with cause";
        Throwable cause = new RuntimeException("Root cause");
        FileStorageException exception = new FileStorageException(message, cause);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
