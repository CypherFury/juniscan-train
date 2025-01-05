package dev.cypherfury.juniscan.repository;

import dev.cypherfury.juniscan.entity.Block;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing `Block` entities.
 * <p>
 * Responsibilities:
 * - Provides CRUD (Create, Read, Update, Delete) operations for `Block` entities.
 * - Extends Spring Data's `CrudRepository` to inherit basic repository functionality.
 * - Acts as a bridge between the application and the database layer for `Block` data.
 * Usage:
 * - Inject this repository wherever database access for `Block` entities is needed.
 * - Define custom query methods if necessary.
 *
 * @author Cypherfury
 */
@Repository
public interface BlockRepository extends CrudRepository<Block, Long> {

    boolean existsByNumber(String number);

}
