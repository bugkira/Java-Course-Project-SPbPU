package org.example.taskmanager.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class EntityAlreadyExistsExceptionTest {

    @Test
    void testConstructorWithMessage() {
        // Given
        String message = "Entity already exists";

        // When
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testInheritance() {
        // When
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException("Test message");

        // Then
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    void testExceptionWithEmptyMessage() {
        // When
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException("");

        // Then
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
    }

    @Test
    void testExceptionStackTrace() {
        // Given
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException("Test exception");

        // When
        StackTraceElement[] stackTrace = exception.getStackTrace();

        // Then
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
    }

    @Test
    void testExceptionToString() {
        // Given
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException("Test exception");

        // When
        String toString = exception.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("EntityAlreadyExistsException"));
        assertTrue(toString.contains("Test exception"));
    }

    @Test
    void testExceptionWithLongMessage() {
        // Given
        String longMessage = "This is a very long exception message that contains a lot of text " +
                "to test how the exception handles longer messages. " +
                "It should be able to store and retrieve messages of various lengths " +
                "without any issues or truncation problems.";

        // When
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException(longMessage);

        // Then
        assertNotNull(exception);
        assertEquals(longMessage, exception.getMessage());
    }

    @Test
    void testExceptionWithSpecialCharacters() {
        // Given
        String specialMessage = "Exception with émojis 🎉 and symbols!@#$%";

        // When
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException(specialMessage);

        // Then
        assertNotNull(exception);
        assertEquals(specialMessage, exception.getMessage());
    }

    @Test
    void testExceptionEquality() {
        // Given
        EntityAlreadyExistsException exception1 = new EntityAlreadyExistsException("Same message");
        EntityAlreadyExistsException exception2 = new EntityAlreadyExistsException("Same message");

        // When & Then
        // Note: Exceptions don't override equals, so they won't be equal even with same message
        assertNotEquals(exception1, exception2);
        assertNotEquals(exception1.hashCode(), exception2.hashCode());
    }

    @Test
    void testExceptionWithDifferentMessages() {
        // Given
        EntityAlreadyExistsException exception1 = new EntityAlreadyExistsException("Message 1");
        EntityAlreadyExistsException exception2 = new EntityAlreadyExistsException("Message 2");

        // When & Then
        assertNotEquals(exception1.getMessage(), exception2.getMessage());
        assertNotEquals(exception1, exception2);
    }
}