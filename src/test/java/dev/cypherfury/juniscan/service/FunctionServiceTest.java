package dev.cypherfury.juniscan.service;

import dev.cypherfury.juniscan.entity.Function;
import dev.cypherfury.juniscan.entity.Module;
import dev.cypherfury.juniscan.exception.FunctionNotFoundException;
import dev.cypherfury.juniscan.repository.FunctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link FunctionService} class.
 * <p>
 * Responsibilities:
 * - Validate retrieval of {@link Function} entities by their associated {@link Module} and call index.
 * - Ensure proper exception handling when a function cannot be found.
 * - Test interactions with the {@link FunctionRepository}.
 *
 * <p>
 * This test ensures that the service correctly manages its repository and gracefully handles missing data.
 *
 * @author Cypher
 */
class FunctionServiceTest {

    private FunctionRepository functionRepository;
    private FunctionService functionService;

    @BeforeEach
    void setUp() {
        functionRepository = mock(FunctionRepository.class);
        functionService = new FunctionService(functionRepository);
    }

    @Test
    void testGetByModuleAndId_Found() {
        // Arrange
        Module mockModule = mock(Module.class);
        Function mockFunction = mock(Function.class);
        int callIndex = 1;

        when(functionRepository.findByModuleAndCallIndex(mockModule, callIndex))
                .thenReturn(Optional.of(mockFunction));

        // Act
        Function result = functionService.getByModuleAndId(mockModule, callIndex);

        // Assert
        assertEquals(mockFunction, result);
        verify(functionRepository).findByModuleAndCallIndex(mockModule, callIndex);
    }

    @Test
    void testGetByModuleAndId_NotFound() {
        // Arrange
        Module mockModule = mock(Module.class);
        int callIndex = 1;

        when(functionRepository.findByModuleAndCallIndex(mockModule, callIndex))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(FunctionNotFoundException.class,
                () -> functionService.getByModuleAndId(mockModule, callIndex));
    }
}
