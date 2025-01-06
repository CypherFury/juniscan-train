package dev.cypherfury.juniscan.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing an Extrinsic in the blockchain.
 * <p>
 * An extrinsic is a payload submitted to the blockchain, representing either a transaction or
 * an inherent operation. This entity is mapped to the database table `extrinsic` and is used
 * to persist and manage extrinsic data within the system.
 * <p>
 * Responsibilities:
 * - Represents an individual extrinsic with its metadata, such as header, version, and size.
 * - Establishes relationships with {@link Block}, {@link Module}, and {@link Function}.
 *
 * @author Cypherfury
 */
@Data
@Table
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Extrinsic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private boolean signed;

    @Column(nullable = false)
    private long size;

    private String value;

    /**
     * The block containing this extrinsic.
     * Represents a many-to-one relationship with the {@link Block} entity.
     */
    @JsonIgnore
    @ManyToOne
    private Block block;

    /**
     * The module to which this extrinsic is tied.
     * Represents a many-to-one relationship with the {@link Module} entity.
     */
    @ManyToOne(cascade = CascadeType.MERGE)
    private Module module;

    /**
     * The function invoked by this extrinsic.
     * Represents a many-to-one relationship with the {@link Function} entity.
     */
    @ManyToOne(cascade = CascadeType.MERGE)
    private Function function;

}
