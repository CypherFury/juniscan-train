package dev.cypherfury.juniscan.utils;

import org.springframework.stereotype.Component;

/**
 * Utility class to encapsulate Thread.sleep().
 * <p>
 * Responsibilities:
 * - Pause the current thread for a specified duration.
 * - Handle any interruptions that occur during the sleep period.
 *
 * @author Cypherfury
 */
@Component
public class Sleeper {

    /**
     * Pauses the current thread for the specified duration.
     *
     * @param millis Duration in milliseconds to pause the thread.
     * @throws InterruptedException If the thread is interrupted while sleeping.
     */
    public void sleep(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }
}
