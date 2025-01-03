package dev.cypherfury.juniscan.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

import static java.text.MessageFormat.format;

/**
 * Custom exception class for handling errors during the processing of WebSocket text messages.
 * <p>
 * This exception is thrown when a {@link JsonProcessingException} occurs while attempting to parse
 * or process the payload of a WebSocket text message. It encapsulates the original exception and
 * provides additional context, including the problematic payload.
 * <p>
 * Responsibilities:
 * - Wraps the {@link JsonProcessingException} with a more descriptive message.
 * - Provides context about the WebSocket payload that caused the failure.
 * <p>
 *
 * @author Cypherfury
 */
public class HandleWebSocketTextException extends RuntimeException {

    /**
     * Constructs a new {@code HandleWebSocketTextException}.
     *
     * @param payload the WebSocket message payload that caused the error.
     * @param e       the underlying {@link JsonProcessingException} that caused the failure.
     */
    public HandleWebSocketTextException(String payload, JsonProcessingException e) {
        super(format("Unable to handle the payload: {0}", payload), e);
    }
    
}
