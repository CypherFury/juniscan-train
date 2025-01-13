package dev.cypherfury.juniscan.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Entity class representing a Function within a Module.
 * <p>
 * A function is a key component of a {@link Module}, defining a specific callable operation that can be invoked
 * as part of an extrinsic. Functions are tied to their respective modules and play a critical role in
 * encapsulating modular logic.
 * <p>
 * Responsibilities:
 * - Represents an individual callable function with a unique identifier and a call index within its module.
 * - Establishes a many-to-one relationship with the {@link Module} entity.
 * - Manages the list of {@link Extrinsic}s associated with this function.
 *
 * @author Cypherfury
 */
@Data
@Table
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Function {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private int callIndex;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    /**
     * The module to which this function belongs.
     * This represents a many-to-one relationship with the {@link Module} entity. Each function is associated
     * with exactly one module, and this relationship is managed through the `module_id` column.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    /**
     * The list of extrinsics associated with this function.
     * This represents a one-to-many relationship with the {@link Extrinsic} entity. Each function can be invoked
     * as part of multiple extrinsics.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "function")
    private List<Extrinsic> extrinsics;


    /**
     * List of parameters associated with this function.
     * <p>
     * Represents a one-to-many relationship between a function and its parameters.
     * Each parameter contains metadata about its name, type, and relationship to the function.
     */
    @OneToMany(mappedBy = "function")
    private List<FunctionParameter> parameters;

}
