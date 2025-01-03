package dev.cypherfury.juniscan.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SleeperTest {

    @Test
    void sleep_shouldPauseWithoutThrowingException() {
        // Arrange
        Sleeper sleeper = new Sleeper();

        // Act & Assert
        assertDoesNotThrow(() -> sleeper.sleep(100));
    }

}