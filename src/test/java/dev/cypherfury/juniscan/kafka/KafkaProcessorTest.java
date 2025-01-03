package dev.cypherfury.juniscan.kafka;

import dev.cypherfury.juniscan.dto.NewHeadDTO;
import dev.cypherfury.juniscan.service.WebSocketNodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.annotation.KafkaListener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link KafkaProcessor} class.
 * <p>
 * Responsibilities:
 * - Validate the processing of NewHeadDTO messages.
 * - Ensure integration with WebSocketNodeService for fetching block details.
 * - Verify the presence and correctness of the @KafkaListener annotation.
 * These tests ensure the functionality and reliability of the NewHeadProcessor.
 *
 * @author Cypherfury
 */
class KafkaProcessorTest {

    @Mock
    private WebSocketNodeService webSocketNodeService;

    @InjectMocks
    private KafkaProcessor kafkaProcessor;

    @Captor
    private ArgumentCaptor<String> blockHashCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessNewHead() {
        // Arrange
        NewHeadDTO newHead = new NewHeadDTO();
        NewHeadDTO.Params params = new NewHeadDTO.Params();
        NewHeadDTO.Params.Result result = new NewHeadDTO.Params.Result();
        result.setParentHash("testParentHash");
        params.setResult(result);
        newHead.setParams(params);

        // Act
        kafkaProcessor.processNewHead(newHead);

        // Assert
        verify(webSocketNodeService, times(1)).fetchBlockDetails(blockHashCaptor.capture());
        assertEquals("testParentHash", blockHashCaptor.getValue());
    }

    @Test
    void testKafkaListenerAnnotation() throws NoSuchMethodException {
        // Arrange
        // (No setup needed for this test)

        // Act
        KafkaListener kafkaListener = KafkaProcessor.class
                .getMethod("processNewHead", NewHeadDTO.class)
                .getAnnotation(KafkaListener.class);

        // Assert
        assertNotNull(kafkaListener);
        assertEquals("chain-new-head", kafkaListener.topics()[0]);
        assertEquals("chain-group", kafkaListener.groupId());
    }
}
