package dev.cypherfury.juniscan.exception;

import java.io.IOException;

import static java.text.MessageFormat.format;

/**
 * Custom exception class for handling errors when sending messages over a WebSocket connection.
 * <p>
 * This exception is thrown when an {@link IOException} occurs during the attempt to send
 * a message via the WebSocket. It encapsulates the original exception and provides additional
 * context, such as the message that caused the failure.
 * <p>
 * Responsibilities:
 * - Wraps the {@link Exception} with a more descriptive message.
 * - Provides context about the message that could not be sent.
 *
 * @author Cypherfury
 */
public class SendSocketMessageException extends RuntimeException {

    /**
     * Constructs a new {@code SendWebSocketMessageException}.
     *
     * @param message the WebSocket message that could not be sent.
     * @param e       the underlying {@link Exception} that caused the failure.
     */
    public SendSocketMessageException(String message, Exception e) {
        super(format("Unable to send the message: {0}", message), e);
    }

}
