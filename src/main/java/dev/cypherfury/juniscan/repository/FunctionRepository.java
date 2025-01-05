package dev.cypherfury.juniscan.repository;

import dev.cypherfury.juniscan.entity.Function;
import dev.cypherfury.juniscan.entity.Module;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing `Function` entities.
 * <p>
 * Responsibilities:
 * - Provides CRUD (Create, Read, Update, Delete) operations for `Function` entities.
 * - Extends Spring Data's `CrudRepository` to inherit basic repository functionality.
 * - Supports custom query methods for finding `Function` entities by module and call index.
 * Usage:
 * - Inject this repository wherever database access for `Function` entities is needed.
 * - Use the {@code findByModuleAndCallIndex} method to retrieve a specific function
 *   associated with a given module and call index.
 *
 * @author Cypherfury
 */
@Repository
public interface FunctionRepository extends CrudRepository<Function, Long> {

    /**
     * Finds a `Function` entity by its associated module and call index.
     *
     * @param module    the `Module` entity associated with the function.
     * @param callIndex the call index of the function within the module.
     * @return an {@code Optional} containing the `Function` entity if found, or empty if not.
     */
    Optional<Function> findByModuleAndCallIndex(Module module, int callIndex);

}
