package dev.cypherfury.juniscan.exception;

/**
 * Custom exception class for handling errors during WebSocket reconnection attempts.
 * <p>
 * This exception is thrown when an {@link InterruptedException} occurs while attempting
 * to reconnect to a WebSocket connection. It encapsulates the original exception and provides
 * a descriptive message for debugging purposes.
 * <p>
 * Responsibilities:
 * - Wraps the {@link InterruptedException} with a more descriptive message.
 * - Indicates that a reconnection attempt has failed.
 * <p>
 *
 * @author Cypherfury
 */
public class ReconnectWebSocketException extends RuntimeException {

    /**
     * Constructs a new {@code ReconnectWebSocketException}.
     *
     * @param e the underlying {@link InterruptedException} that caused the reconnection failure.
     */
    public ReconnectWebSocketException(InterruptedException e) {
        super("Unable to reconnect to the WebSocket connection.", e);
    }

}
