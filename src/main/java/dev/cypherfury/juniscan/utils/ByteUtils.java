package dev.cypherfury.juniscan.utils;

import dev.cypherfury.juniscan.exception.GetBytesException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import static java.text.MessageFormat.format;

/**
 * Utility class for handling hexadecimal string conversions.
 * Provides methods to clean, validate, and decode hexadecimal strings into byte arrays.
 * This class ensures input hex strings are correctly formatted and converts them into
 * their corresponding byte representation.
 * Typical usage:
 * <pre>
 *     String hex = "0xdeadbeef";
 *     byte[] bytes = ByteUtils.getBytes(hex);
 * </pre>
 *
 * @author cypherfury
 */
@Slf4j
public class ByteUtils {

    public static final String HEX_PREFIX = "0x";
    public static final String VALID_HEX_REGEX = "[0-9a-fA-F]+";
    public static final String INVALID_HEX_STRING = "Invalid hex string: {0}";
    public static final String ERROR_DECODING = "Error decoding hex string: {}, reason: {}";
    public static final String NULL_HEX_STRING = "Hex string cannot be null";

    /**
     * Converts a hex string to a byte array after cleaning and validation.
     *
     * @param hexString the input hex string, optionally prefixed with "0x"
     * @return the decoded byte array
     * @throws IllegalArgumentException if the hex string is invalid
     * @throws GetBytesException        if decoding fails due to a parsing error
     */
    public static byte[] getBytes(String hexString) {
        try {
            return cleanAndValid(hexString);
        } catch (DecoderException e) {
            log.error(ERROR_DECODING, hexString, e.getMessage(), e);
            throw new GetBytesException(e, hexString);
        }
    }

    /**
     * Cleans the hex string (removing prefix) and validates it before decoding.
     *
     * @param hexString the input hex string
     * @return a valid decoded byte array
     * @throws IllegalArgumentException if the hex string is invalid
     */
    private static byte[] cleanAndValid(String hexString) throws DecoderException {
        String cleanedHex = cleanHexPrefix(hexString);
        if (!isValidHex(cleanedHex)) {
            throw new IllegalArgumentException(format(INVALID_HEX_STRING, cleanedHex));
        }
        return Hex.decodeHex(cleanedHex);
    }

    /**
     * Removes the "0x" prefix if present.
     *
     * @param hexString the input hex string
     * @return the cleaned hex string
     * @throws IllegalArgumentException if the input is null
     */
    private static String cleanHexPrefix(String hexString) {
        if (hexString == null) {
            throw new IllegalArgumentException(NULL_HEX_STRING);
        }
        return hexString.startsWith(HEX_PREFIX) ?
                hexString.substring(HEX_PREFIX.length()) :
                hexString;
    }

    /**
     * Checks if the string is a valid hex string (only 0-9, a-f, A-F).
     *
     * @param string the input string
     * @return true if the string is a valid hex string
     */
    private static boolean isValidHex(String string) {
        return string.matches(VALID_HEX_REGEX);
    }

}
