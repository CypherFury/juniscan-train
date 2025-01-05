package dev.cypherfury.juniscan.service;

import dev.cypherfury.juniscan.entity.Function;
import dev.cypherfury.juniscan.entity.Module;
import dev.cypherfury.juniscan.exception.FunctionNotFoundException;
import dev.cypherfury.juniscan.repository.FunctionRepository;
import org.springframework.stereotype.Service;

/**
 * Service class for managing `Function` entities.
 * <p>
 * Responsibilities:
 * - Retrieves `Function` entities by their associated module and call index.
 * - Handles cases where a function cannot be found with a custom exception.
 *
 * @author Cypherfury
 */
@Service
public class FunctionService {

    private final FunctionRepository functionRepository;

    /**
     * Constructor for `FunctionService`.
     *
     * @param functionRepository repository for interacting with `Function` entities.
     */
    public FunctionService(FunctionRepository functionRepository) {
        this.functionRepository = functionRepository;
    }

    /**
     * Retrieves a `Function` by its associated module and call index.
     *
     * @param module    the `Module` entity to which the function belongs.
     * @param callIndex the call index of the function.
     * @return the retrieved `Function` entity.
     * @throws FunctionNotFoundException if no function matches the given parameters.
     */
    public Function getByModuleAndId(Module module, int callIndex) {
        return functionRepository.findByModuleAndCallIndex(module, callIndex)
                .orElseThrow(() -> new FunctionNotFoundException(module, callIndex));
    }

}
