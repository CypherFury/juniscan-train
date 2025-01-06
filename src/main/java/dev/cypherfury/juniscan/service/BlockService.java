package dev.cypherfury.juniscan.service;

import dev.cypherfury.juniscan.dto.BlockDetailsDTO;
import dev.cypherfury.juniscan.entity.Block;
import dev.cypherfury.juniscan.entity.Extrinsic;
import dev.cypherfury.juniscan.repository.BlockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing `Block` entities.
 * <p>
 * Responsibilities:
 * - Handles the decoding and persistence of blockchain blocks.
 * - Coordinates with the `ExtrinsicService` to process extrinsics within a block.
 * - Provides utility methods for building and verifying blocks.
 *
 * @author Cypherfury
 */
@Service
public class BlockService {

    private final BlockRepository blockRepository;
    private final ExtrinsicService extrinsicService;

    /**
     * Constructor for `BlockService`.
     *
     * @param blockRepository  repository for interacting with `Block` entities.
     * @param extrinsicService service for decoding and managing extrinsics.
     */
    public BlockService(BlockRepository blockRepository, ExtrinsicService extrinsicService) {
        this.blockRepository = blockRepository;
        this.extrinsicService = extrinsicService;
    }

    /**
     * Decodes block details and saves the block if it does not already exist.
     *
     * @param blockDTO DTO containing block details for processing.
     */
    public void decodeAndSave(BlockDetailsDTO.Block blockDTO) {
        List<Extrinsic> extrinsics = extrinsicService.decode(List.of(blockDTO.getExtrinsics()));
        Block block = build(blockDTO, extrinsics);
        extrinsics.forEach(extrinsic -> extrinsic.setBlock(block));
        blockRepository.save(block);
    }

    /**
     * Checks if a block with the given number already exists.
     *
     * @param block the block details containing the number to check.
     * @return `true` if the block exists, `false` otherwise.
     */
    public boolean alreadyExist(BlockDetailsDTO.Block block) {
        return blockRepository.existsByNumber(block.getHeader().getNumber());
    }

    /**
     * Find all saved blocks
     *
     * @return an Iterable of Blocks.
     */
    public Iterable<Block> findAll() {
        return blockRepository.findAll();
    }

    /**
     * Builds a `Block` entity from block details and associated extrinsics.
     *
     * @param blockDTO DTO containing the block header and extrinsics data.
     * @param extrinsics   list of decoded extrinsics for the block.
     * @return the constructed `Block` entity.
     */
    private Block build(BlockDetailsDTO.Block blockDTO, List<Extrinsic> extrinsics) {
        return Block.builder()
                .parentHash(blockDTO.getHeader().getParentHash())
                .logs(List.of(blockDTO.getHeader().getDigest().getLogs()))
                .extrinsicsRoot(blockDTO.getHeader().getExtrinsicsRoot())
                .stateRoot(blockDTO.getHeader().getStateRoot())
                .number(blockDTO.getHeader().getNumber())
                .extrinsics(extrinsics)
                .build();
    }

}
