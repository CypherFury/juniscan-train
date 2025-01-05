package dev.cypherfury.juniscan.service;

import dev.cypherfury.juniscan.entity.Module;
import dev.cypherfury.juniscan.exception.ModuleNotFoundException;
import dev.cypherfury.juniscan.repository.ModuleRepository;
import org.springframework.stereotype.Service;

/**
 * Service class for managing `Module` entities.
 * <p>
 * Responsibilities:
 * - Retrieves `Module` entities by their ID.
 * - Handles cases where a module cannot be found with a custom exception.
 *
 * @author Cypherfury
 */
@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;

    /**
     * Constructor for `ModuleService`.
     *
     * @param moduleRepository repository for interacting with `Module` entities.
     */
    public ModuleService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    /**
     * Retrieves a `Module` by its unique ID.
     *
     * @param id the unique identifier of the module.
     * @return the retrieved `Module` entity.
     * @throws ModuleNotFoundException if no module matches the given ID.
     */
    public Module getById(long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ModuleNotFoundException(id));
    }
}