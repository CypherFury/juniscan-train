package dev.cypherfury.juniscan.utils;

import dev.cypherfury.juniscan.exception.GetBytesException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for the {@link dev.cypherfury.juniscan.utils.ByteUtils} class.
 * <p>
 * These tests cover:
 * - Valid conversions of hexadecimal strings to byte arrays.
 * - Handling of edge cases like null, invalid hex strings, and odd-length inputs.
 * - Validation of error handling and exception throwing for invalid inputs.
 * </p>
 *
 * @author cypherfury
 */
class ByteUtilsTest {

    @Test
    void testValidHexStringWithoutPrefix() {
        // Arrange
        String hex = "deadbeef";

        // Act
        byte[] result = ByteUtils.getBytes(hex);

        // Assert
        assertThat(result).containsExactly((byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF);
    }

    @Test
    void testValidHexStringWithPrefix() {
        // Arrange
        String hex = "0xdeadbeef";

        // Act
        byte[] result = ByteUtils.getBytes(hex);

        // Assert
        assertThat(result).containsExactly((byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF);
    }

    @Test
    void testUpperCaseHex() {
        // Arrange
        String hex = "DEADBEEF";

        // Act
        byte[] result = ByteUtils.getBytes(hex);

        // Assert
        assertThat(result).containsExactly((byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF);
    }

    @Test
    void testMixedCaseHex() {
        // Arrange
        String hex = "DeadBeEf";

        // Act
        byte[] result = ByteUtils.getBytes(hex);

        // Assert
        assertThat(result).containsExactly((byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF);
    }

    @Test
    void testEmptyStringThrowsException() {
        // Arrange
        String hex = "";

        // Act & Assert
        assertThatThrownBy(() -> ByteUtils.getBytes(hex))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid hex string: ");
    }

    @Test
    void testNullStringThrowsException() {
        // Arrange
        String hex = null;

        // Act & Assert
        assertThatThrownBy(() -> ByteUtils.getBytes(hex))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Hex string cannot be null");
    }

    @Test
    void testInvalidHexStringThrowsException() {
        // Arrange
        String hex = "0xg123";

        // Act & Assert
        assertThatThrownBy(() -> ByteUtils.getBytes(hex))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid hex string: g123");
    }

    @Test
    void testValidSingleByteHexString() {
        // Arrange
        String hex = "0A";

        // Act
        byte[] result = ByteUtils.getBytes(hex);

        // Assert
        assertThat(result).containsExactly((byte) 0x0A);
    }

    @Test
    void testValidMultiByteHexString() {
        // Arrange
        String hex = "0x010203";

        // Act
        byte[] result = ByteUtils.getBytes(hex);

        // Assert
        assertThat(result).containsExactly((byte) 0x01, (byte) 0x02, (byte) 0x03);
    }

    @Test
    void testHexWithOddLengthThrowsException() {
        // Arrange
        String hex = "0x123";

        // Act & Assert
        assertThatThrownBy(() -> ByteUtils.getBytes(hex))
                .isInstanceOf(GetBytesException.class)
                .hasMessageContaining("0x123");
    }
}
