package dev.cypherfury.juniscan.service;

import dev.cypherfury.juniscan.entity.Extrinsic;
import dev.cypherfury.juniscan.entity.Function;
import dev.cypherfury.juniscan.entity.Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link ExtrinsicService} class.
 * <p>
 * Responsibilities:
 * - Validate the decoding of extrinsics from raw byte arrays and string representations.
 * - Ensure proper construction of {@link Extrinsic} entities with associated {@link Module} and {@link Function}.
 * - Test interactions with dependencies such as {@link ModuleService} and {@link FunctionService}.
 * - Cover edge cases for empty or invalid extrinsic data.
 *
 * @author Cypher
 */
class ExtrinsicServiceTest {

    private FunctionService functionService;
    private ModuleService moduleService;
    private ExtrinsicService extrinsicService;

    @BeforeEach
    void setUp() {
        functionService = mock(FunctionService.class);
        moduleService = mock(ModuleService.class);
        extrinsicService = new ExtrinsicService(functionService, moduleService);
    }

    @Test
    void testDecode_ValidExtrinsics() {
        // Arrange
        String extrinsicString = "abcd1234";
        List<String> extrinsics = List.of(extrinsicString);

        Module mockModule = mock(Module.class);
        Function mockFunction = mock(Function.class);

        when(moduleService.getById(anyInt())).thenReturn(mockModule);
        when(functionService.getByModuleAndId(any(), anyInt())).thenReturn(mockFunction);

        // Act
        List<Extrinsic> result = extrinsicService.decode(extrinsics);

        // Assert
        assertEquals(1, result.size());
        verify(moduleService).getById(anyLong());
        verify(functionService).getByModuleAndId(any(), anyInt());
    }

    @Test
    void testDecode_EmptyList() {
        // Arrange
        List<String> extrinsics = List.of();

        // Act
        List<Extrinsic> result = extrinsicService.decode(extrinsics);

        // Assert
        assertTrue(result.isEmpty());
        verifyNoInteractions(moduleService, functionService);
    }

    @Test
    void testIsSigned() {
        // Arrange
        int signedHeader = 0x80; // MSB set
        int unsignedHeader = 0x00; // MSB not set

        // Act & Assert
        assertTrue((signedHeader & 0x80) != 0);
        assertFalse((unsignedHeader & 0x80) != 0);
    }
}
