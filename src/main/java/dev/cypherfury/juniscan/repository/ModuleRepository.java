package dev.cypherfury.juniscan.repository;

import dev.cypherfury.juniscan.entity.Module;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing `Module` entities.
 * <p>
 * Responsibilities:
 * - Provides CRUD (Create, Read, Update, Delete) operations for `Module` entities.
 * - Extends Spring Data's `CrudRepository` to inherit basic repository functionality.
 * Usage:
 * - Inject this repository wherever database access for `Module` entities is needed.
 * - Use standard CRUD methods or define additional custom query methods as needed.
 *
 * @author Cypherfury
 */
@Repository
public interface ModuleRepository extends CrudRepository<Module, Long> {
}
