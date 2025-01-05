package dev.cypherfury.juniscan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Entity class representing a Block in the blockchain.
 * <p>
 * A block is a fundamental structure in a blockchain that contains information about its parent,
 * state, extrinsics, and other metadata. This entity is mapped to the database table `block` and
 * is used to persist and manage block data within the system.
 * <p>
 * Responsibilities:
 * - Represents a block with its unique identifier and metadata, such as the parent hash and state root.
 * - Manages the collection of logs associated with the block.
 * - Establishes a one-to-many relationship with {@link Extrinsic}s contained within the block.
 *
 * @author Cypherfury
 */
@Data
@Table
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String parentHash;

    @Column(nullable = false, unique = true)
    private String number;

    @Column(nullable = false)
    private String stateRoot;

    @Column(nullable = false)
    private String extrinsicsRoot;

    /**
     * A collection of logs associated with this block.
     * Logs provide additional information or metadata about the block's execution.
     */
    @ElementCollection
    private List<String> logs;

    /**
     * The list of extrinsics contained in this block.
     * Represents a one-to-many relationship with the {@link Extrinsic} entity, where each block can
     * contain multiple extrinsics.
     */
    @OneToMany(mappedBy = "block", cascade = CascadeType.PERSIST)
    private List<Extrinsic> extrinsics;

}
