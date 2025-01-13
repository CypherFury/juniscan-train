package dev.cypherfury.juniscan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a parameter of a function in the blockchain.
 * <p>
 * This entity is mapped to the database table `function_parameters` and is used
 * to persist and manage function parameters within the system.
 * <p>
 * Responsibilities:
 * - Stores metadata about a parameter, including its name and type.
 * - Establishes relationships with the {@link Function} entity.
 *
 * @author Cypherfury
 */
@Data
@Table
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunctionParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    /**
     * The function associated with this parameter.
     * Represents a many-to-one relationship with the {@link Function} entity.
     */
    @ManyToOne
    private Function function;

}

