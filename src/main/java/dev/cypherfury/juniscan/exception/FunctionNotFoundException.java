package dev.cypherfury.juniscan.exception;

import dev.cypherfury.juniscan.entity.Module;

import static java.text.MessageFormat.format;

/**
 * Custom exception class for handling errors related to missing functions in a specific module.
 * <p>
 * This exception is thrown when a function corresponding to a specific module ID and call index
 * cannot be found in the system.
 * <p>
 * Responsibilities:
 * - Provides a descriptive error message for missing function lookups.
 * - Encapsulates context about the module and call index that caused the error.
 *
 * @author Cypherfury
 */
public class FunctionNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code FunctionNotFoundException}.
     *
     * @param module    the {@link Module} object where the function was expected to be found.
     * @param callIndex the call index of the function within the module.
     */
    public FunctionNotFoundException(Module module, int callIndex) {
        super(format("Unable to find the function for the module with id {0} and call index {1}.", module.getId(), callIndex));
    }

}
