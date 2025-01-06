package dev.cypherfury.juniscan.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Entity class representing a Module as part of the extrinsics in the system.
 * <p>
 * A module is a foundational component of extrinsics, encapsulating a set of functionalities defined as {@link Function}s.
 * It plays a key role in structuring and managing extrinsics-related data within the system.
 * This entity is mapped to the database table `module` and is used to persist and manage module-related data.
 * <p>
 * Responsibilities:
 * - Represents a module with a unique identifier, name, and optional description.
 * - Manages the relationship with {@link Function}s and {@link Extrinsic}s associated with this module.
 * - Enforces constraints such as unique names at the database level.
 *
 * @author Cypherfury
 */
@Data
@Table
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Module {

    @Id
    @Column(nullable = false, unique = true)
    private long id;


    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    /**
     * The list of functions associated with this module.
     * This represents a one-to-many relationship with the {@link Function} entity. Functions are managed
     * as children of the module:
     * - Cascading operations ensure that creating, updating, or deleting a module also propagates to its functions.
     * - Orphan removal ensures that functions no longer associated with a module are automatically deleted.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Function> functions;

    /**
     * The list of extrinsics associated with this module.
     * This represents a one-to-many relationship with the {@link Extrinsic} entity. Each module can have
     * multiple extrinsics tied to it, and this relationship is critical to the overall extrinsic structure.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "module")
    private List<Extrinsic> extrinsics;

}
