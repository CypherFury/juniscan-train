package dev.cypherfury.juniscan.repository;

import dev.cypherfury.juniscan.entity.Extrinsic;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing `Extrinsic` entities.
 * <p>
 * Responsibilities:
 * - Provides CRUD (Create, Read, Update, Delete) operations for `Extrinsic` entities.
 * - Extends Spring Data's `CrudRepository` to inherit basic repository functionality.
 * - Acts as a bridge between the application and the database layer for `Extrinsic` data.
 * Usage:
 * - Inject this repository wherever database access for `Extrinsic` entities is needed.
 * - Define custom query methods if necessary.
 *
 * @author Cypherfury
 */
@Repository
public interface ExtrinsicRepository extends CrudRepository<Extrinsic, Long> {
}
