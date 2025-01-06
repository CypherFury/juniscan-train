package dev.cypherfury.juniscan.controller;

import dev.cypherfury.juniscan.entity.Block;
import dev.cypherfury.juniscan.service.BlockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing `Block` entities.
 * <p>
 * Responsibilities:
 * - Exposes endpoints for interacting with `Block` entities.
 * - Delegates business logic to the `BlockService` layer.
 *
 * @author Cypherfury
 */
@RestController
@RequestMapping("/block")
public class BlockController {

    private final BlockService blockService;

    /**
     * Constructor for `BlockController`.
     *
     * @param blockService service for managing and retrieving `Block` entities.
     */
    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    /**
     * Endpoint to retrieve all `Block` entities.
     * <p>
     * This endpoint handles GET requests to `/block` and returns an iterable collection
     * of all blocks stored in the system.
     *
     * @return an iterable collection of `Block` entities.
     */
    @GetMapping
    public Iterable<Block> getBlocks() {
        return blockService.findAll();
    }

}
