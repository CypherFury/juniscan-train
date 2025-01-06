package dev.cypherfury.juniscan.controller;

import dev.cypherfury.juniscan.entity.Block;
import dev.cypherfury.juniscan.service.BlockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the {@link BlockController} class.
 * <p>
 * Responsibilities:
 * - Validate HTTP endpoints exposed by the controller.
 * - Ensure interactions with the {@link BlockService} are correct.
 * - Verify proper HTTP responses and status codes.
 *
 * @author Cypherfury
 */
class BlockControllerTest {

    private MockMvc mockMvc;
    private BlockService blockService;

    @BeforeEach
    void setUp() {
        blockService = Mockito.mock(BlockService.class);
        BlockController blockController = new BlockController(blockService);
        mockMvc = MockMvcBuilders.standaloneSetup(blockController).build();
    }

    @Test
    void testGetBlocks_ReturnsBlocks() throws Exception {
        // Arrange
        Block block1 = new Block();
        block1.setId(1L);
        block1.setNumber("100");

        Block block2 = new Block();
        block2.setId(2L);
        block2.setNumber("101");

        when(blockService.findAll()).thenReturn(List.of(block1, block2));

        // Act & Assert
        mockMvc.perform(get("/block")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].number").value("100"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].number").value("101"));

        verify(blockService, times(1)).findAll();
    }

    @Test
    void testGetBlocks_EmptyResponse() throws Exception {
        // Arrange
        when(blockService.findAll()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/block")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[]")); // Empty array in JSON response

        verify(blockService, times(1)).findAll();
    }
}
