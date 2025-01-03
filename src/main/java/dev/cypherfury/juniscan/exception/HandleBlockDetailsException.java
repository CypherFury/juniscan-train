package dev.cypherfury.juniscan.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import static java.text.MessageFormat.format;

/**
 * Custom exception class for handling errors during the processing of block details.
 * <p>
 * This exception is thrown when a {@link JsonProcessingException} occurs while attempting to process
 * a block's details from a {@link JsonNode}. It encapsulates the original exception and provides
 * additional context about the problematic JSON node.
 * <p>
 * Responsibilities:
 * - Wraps the {@link JsonProcessingException} with a more descriptive message.
 * - Provides context by including the JSON node that caused the failure.
 * <p>
 *
 * @author Cypherfury
 */
public class HandleBlockDetailsException extends RuntimeException {

    /**
     * Constructs a new {@code HandleBlockDetailsException}.
     *
     * @param jsonNode the {@link JsonNode} representing the block details that caused the error.
     * @param e        the underlying {@link JsonProcessingException} that caused the failure.
     */
    public HandleBlockDetailsException(JsonNode jsonNode, JsonProcessingException e) {
        super(format("Unhandled block details exception: {0}", jsonNode.toString()), e);
    }

}
