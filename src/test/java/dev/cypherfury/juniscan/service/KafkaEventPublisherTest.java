package dev.cypherfury.juniscan.service;

import dev.cypherfury.juniscan.dto.NewHeadDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link KafkaEventPublisher} class.
 * <p>
 * Responsibilities:
 * - Validate the correct publishing of NewHeadDTO messages to Kafka.
 * - Ensure the KafkaTemplate is used appropriately.
 * - Capture and verify the correctness of the published NewHeadDTO objects.
 * These tests ensure the reliability of the Kafka publishing mechanism.
 *
 * @author Cypherfury
 */
class KafkaEventPublisherTest {

    @Mock
    private KafkaTemplate<String, NewHeadDTO> kafkaTemplate;

    @InjectMocks
    private KafkaEventPublisher kafkaEventPublisher;

    @Captor
    private ArgumentCaptor<NewHeadDTO> newHeadCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPublishNewHead() {
        // Arrange
        NewHeadDTO mockNewHead = new NewHeadDTO();
        mockNewHead.setJsonrpc("2.0");
        mockNewHead.setMethod("chain_newHead");

        // Act
        kafkaEventPublisher.publishNewHead(mockNewHead);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq(KafkaEventPublisher.NEW_HEAD_TOPIC), newHeadCaptor.capture());
        NewHeadDTO capturedNewHead = newHeadCaptor.getValue();
        assertEquals("2.0", capturedNewHead.getJsonrpc());
        assertEquals("chain_newHead", capturedNewHead.getMethod());
    }

    @Test
    void testPublishNewHeadLogsMessage() {
        // Arrange
        NewHeadDTO mockNewHead = new NewHeadDTO();
        mockNewHead.setJsonrpc("2.0");
        mockNewHead.setMethod("chain_newHead");

        // Act
        kafkaEventPublisher.publishNewHead(mockNewHead);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq(KafkaEventPublisher.NEW_HEAD_TOPIC), any(NewHeadDTO.class));
    }
}
