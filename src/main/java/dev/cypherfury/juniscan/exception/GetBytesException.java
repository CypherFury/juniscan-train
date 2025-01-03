package dev.cypherfury.juniscan.exception;

import org.apache.commons.codec.DecoderException;

import static java.text.MessageFormat.format;

/**
 * Custom exception class for handling errors related to byte decoding operations.
 * <p>
 * This exception is thrown when a string cannot be successfully converted to its byte representation,
 * encapsulating the root cause of the failure (e.g., a {@link DecoderException}).
 * <p>
 * Responsibilities:
 * - Wraps the original {@link DecoderException} with a more descriptive message.
 * - Provides context about the string that caused the error.
 *
 * @author Cypherfury
 */
public class GetBytesException extends RuntimeException {

    /**
     * Constructs a new {@code GetBytesException}.
     *
     * @param e      the underlying {@link DecoderException} that caused this error.
     * @param string the string that could not be converted to bytes.
     */
    public GetBytesException(DecoderException e, String string) {
        super(format("Unable to get bytes for string {0}", string), e);
    }

}
