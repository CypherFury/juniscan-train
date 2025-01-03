package dev.cypherfury.juniscan.exception;

import org.springframework.web.socket.WebSocketSession;

import static java.text.MessageFormat.format;

/**
 * Custom exception class for handling transport-level errors in a WebSocket session.
 * <p>
 * This exception is thrown when a transport-level error occurs during a WebSocket session.
 * It encapsulates the original {@link Throwable} exception and provides additional context
 * about the WebSocket session where the error occurred.
 * <p>
 * Responsibilities:
 * - Wraps the original exception with a more descriptive message.
 * - Provides context by including the WebSocket session ID.
 * <p>
 *
 * @author Cypherfury
 */
public class HandleTransportException extends RuntimeException {

    /**
     * Constructs a new {@code HandleTransportException}.
     *
     * @param session   the {@link WebSocketSession} where the transport error occurred.
     * @param exception the original {@link Throwable} that caused the transport error.
     */
    public HandleTransportException(WebSocketSession session, Throwable exception) {
        super(format("Unable to handle transport for session: {0}", session.getId()), exception);
    }

}
