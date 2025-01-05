package dev.cypherfury.juniscan.exception;

import static java.text.MessageFormat.format;

/**
 * Custom exception class for handling errors related to missing modules.
 * <p>
 * This exception is thrown when a module with a specific ID cannot be found in the system.
 * <p>
 * Responsibilities:
 * - Provides a descriptive error message for missing module lookups.
 * - Encapsulates context about the module ID that caused the error.
 *
 * <p>
 * Features:
 * - Includes the module ID in the error message for easier debugging and tracing.
 *
 * @author Cypherfury
 */
public class ModuleNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code ModuleNotFoundException}.
     *
     * @param id the unique identifier of the missing module.
     */
    public ModuleNotFoundException(long id) {
        super(format("Module with id {0} not found.", id));
    }

}
