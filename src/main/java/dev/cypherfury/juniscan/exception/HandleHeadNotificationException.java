package dev.cypherfury.juniscan.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import static java.text.MessageFormat.format;

/**
 * Custom exception class for handling errors during the processing of head notifications.
 * <p>
 * This exception is thrown when a {@link JsonProcessingException} occurs while attempting to process
 * a new head notification from a {@link JsonNode}. It encapsulates the original exception and provides
 * additional context about the problematic JSON node.
 * <p>
 * Responsibilities:
 * - Wraps the {@link JsonProcessingException} with a more descriptive message.
 * - Provides context by including the JSON node that caused the failure.
 * <p>
 *
 * @author Cypherfury
 */
public class HandleHeadNotificationException extends RuntimeException {

    /**
     * Constructs a new {@code HandleHeadNotificationException}.
     *
     * @param jsonNode the {@link JsonNode} representing the head notification that caused the error.
     * @param e        the underlying {@link JsonProcessingException} that caused the failure.
     */
    public HandleHeadNotificationException(JsonNode jsonNode, JsonProcessingException e) {
        super(format("Unhandled head notification exception: {0}", jsonNode.toString()), e);
    }

}
