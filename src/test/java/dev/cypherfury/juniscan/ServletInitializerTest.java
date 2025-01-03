package dev.cypherfury.juniscan;

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ServletInitializerTest {

    @Test
    void configure_shouldReturnConfiguredSpringApplicationBuilder() {
        // Arrange
        ServletInitializer initializer = new ServletInitializer();
        SpringApplicationBuilder builderMock = mock(SpringApplicationBuilder.class);

        when(builderMock.sources(JuniscanApplication.class)).thenReturn(builderMock);

        // Act
        SpringApplicationBuilder result = initializer.configure(builderMock);

        // Assert
        assertNotNull(result);
        verify(builderMock).sources(JuniscanApplication.class);
    }

}

