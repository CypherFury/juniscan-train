package dev.cypherfury.juniscan.service;

import dev.cypherfury.juniscan.dto.BlockDetailsDTO;
import dev.cypherfury.juniscan.entity.Block;
import dev.cypherfury.juniscan.entity.Extrinsic;
import dev.cypherfury.juniscan.repository.BlockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link BlockService} class.
 * <p>
 * Responsibilities:
 * - Test the decoding and persistence of blocks.
 * - Verify interactions with dependencies such as {@link BlockRepository} and {@link ExtrinsicService}.
 * - Validate utility methods for block management.
 *
 * @author Cypherfury
 */
class BlockServiceTest {

    private BlockRepository blockRepository;
    private ExtrinsicService extrinsicService;
    private BlockService blockService;

    @BeforeEach
    void setUp() {
        blockRepository = mock(BlockRepository.class);
        extrinsicService = mock(ExtrinsicService.class);
        blockService = new BlockService(blockRepository, extrinsicService);
    }

    @Test
    void testDecodeAndSave_WhenBlockDoesNotExist() {
        // Arrange
        BlockDetailsDTO.Block blockDTO = mock(BlockDetailsDTO.Block.class);
        BlockDetailsDTO.Block.Header header = mock(BlockDetailsDTO.Block.Header.class);
        BlockDetailsDTO.Block.Header.Digest digest = mock(BlockDetailsDTO.Block.Header.Digest.class);
        when(blockDTO.getHeader()).thenReturn(header);
        when(blockDTO.getHeader().getDigest()).thenReturn(digest);
        when(blockDTO.getHeader().getDigest().getLogs()).thenReturn(new String[]{"log"});
        when(header.getNumber()).thenReturn("123");
        when(blockDTO.getExtrinsics()).thenReturn(new String[]{"extrinsic-data"});
        when(extrinsicService.decode(List.of("extrinsic-data"))).thenReturn(Collections.singletonList(mock(Extrinsic.class)));
        when(blockRepository.existsByNumber("123")).thenReturn(false);

        // Act
        blockService.decodeAndSave(blockDTO);

        // Assert
        verify(blockRepository, times(1)).save(any(Block.class));
    }

    @Test
    void testAlreadyExist_WhenBlockExists() {
        // Arrange
        BlockDetailsDTO.Block blockDTO = mock(BlockDetailsDTO.Block.class);
        BlockDetailsDTO.Block.Header header = mock(BlockDetailsDTO.Block.Header.class);
        when(blockDTO.getHeader()).thenReturn(header);
        when(header.getNumber()).thenReturn("123");
        when(blockRepository.existsByNumber("123")).thenReturn(true);

        // Act
        boolean exists = blockService.alreadyExist(blockDTO);

        // Assert
        assertTrue(exists);
    }

    @Test
    void testAlreadyExist_WhenBlockDoesNotExist() {
        // Arrange
        BlockDetailsDTO.Block blockDTO = mock(BlockDetailsDTO.Block.class);
        BlockDetailsDTO.Block.Header header = mock(BlockDetailsDTO.Block.Header.class);
        when(blockDTO.getHeader()).thenReturn(header);
        when(header.getNumber()).thenReturn("123");
        when(blockRepository.existsByNumber("123")).thenReturn(false);

        // Act
        boolean exists = blockService.alreadyExist(blockDTO);

        // Assert
        assertFalse(exists);
    }

    @Test
    void testFindAll() {
        // Arrange
        Iterable<Block> blocks = List.of(mock(Block.class), mock(Block.class));
        when(blockRepository.findAll()).thenReturn(blocks);

        // Act
        Iterable<Block> result = blockService.findAll();

        // Assert
        assertEquals(blocks, result);
        verify(blockRepository, times(1)).findAll();
    }

    @Test
    void testBuildBlock() {
        // Arrange
        BlockDetailsDTO.Block blockDTO = mock(BlockDetailsDTO.Block.class);
        BlockDetailsDTO.Block.Header header = mock(BlockDetailsDTO.Block.Header.class);
        when(blockDTO.getHeader()).thenReturn(header);
        when(header.getParentHash()).thenReturn("parent-hash");
        when(header.getExtrinsicsRoot()).thenReturn("extrinsics-root");
        when(header.getStateRoot()).thenReturn("state-root");
        when(header.getNumber()).thenReturn("block-number");
        when(header.getDigest()).thenReturn(mock(BlockDetailsDTO.Block.Header.Digest.class));
        when(header.getDigest().getLogs()).thenReturn(new String[]{"log1", "log2"});
        List<Extrinsic> extrinsics = List.of(mock(Extrinsic.class), mock(Extrinsic.class));

        // Act
        Block result = ReflectionTestUtils.invokeMethod(blockService, "build", blockDTO, extrinsics);

        // Assert
        assertNotNull(result);
        assertEquals("parent-hash", result.getParentHash());
        assertEquals("extrinsics-root", result.getExtrinsicsRoot());
        assertEquals("state-root", result.getStateRoot());
        assertEquals("block-number", result.getNumber());
        assertEquals(List.of("log1", "log2"), result.getLogs());
        assertEquals(extrinsics, result.getExtrinsics());
    }
}
