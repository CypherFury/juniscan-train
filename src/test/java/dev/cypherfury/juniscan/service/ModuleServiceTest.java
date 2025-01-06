package dev.cypherfury.juniscan.service;

import dev.cypherfury.juniscan.entity.Module;
import dev.cypherfury.juniscan.exception.ModuleNotFoundException;
import dev.cypherfury.juniscan.repository.ModuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link ModuleService} class.
 * <p>
 * Responsibilities:
 * - Validate retrieval of {@link Module} entities by their unique ID.
 * - Ensure proper exception handling when a module cannot be found.
 * - Test interactions with the {@link ModuleRepository}.
 *
 * @author Cypherfury
 */
class ModuleServiceTest {


    private ModuleRepository moduleRepository;
    private ModuleService moduleService;

    @BeforeEach
    void setUp() {
        moduleRepository = mock(ModuleRepository.class);
        moduleService = new ModuleService(moduleRepository);
    }

    @Test
    void testGetById_Found() {
        // Arrange
        long moduleId = 1L;
        Module mockModule = mock(Module.class);

        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(mockModule));

        // Act
        Module result = moduleService.getById(moduleId);

        // Assert
        assertEquals(mockModule, result);
        verify(moduleRepository).findById(moduleId);
    }

    @Test
    void testGetById_NotFound() {
        // Arrange
        long moduleId = 1L;

        when(moduleRepository.findById(moduleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ModuleNotFoundException.class, () -> moduleService.getById(moduleId));
    }
}
