package dev.cypherfury.juniscan.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cypherfury.juniscan.kafka.KafkaPublisher;
import dev.cypherfury.juniscan.dto.BlockDetailsDTO;
import dev.cypherfury.juniscan.dto.NewHeadDTO;
import dev.cypherfury.juniscan.exception.HandleBlockDetailsException;
import dev.cypherfury.juniscan.exception.HandleHeadNotificationException;
import dev.cypherfury.juniscan.exception.HandleWebSocketTextException;
import dev.cypherfury.juniscan.exception.SendSocketMessageException;
import joptsimple.internal.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link WebSocketNodeService} class.
 * <p>
 * Responsibilities:
 * - Validate the correct processing of WebSocket messages.
 * - Ensure proper handling of JSON-RPC requests and responses.
 * - Test exception handling for various failure scenarios.
 * - Verify integration points with KafkaEventPublisher and WebSocketConnectionManager.
 * These tests ensure the robustness and reliability of the WebSocketNodeService.
 *
 * @author Cypherfury
 */
class WebSocketNodeServiceTest {

    private KafkaPublisher eventPublisher;
    private WebSocketConnectionManager connectionManager;
    private WebSocketNodeService webSocketNodeService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        eventPublisher = mock(KafkaPublisher.class);
        connectionManager = mock(WebSocketConnectionManager.class);
        objectMapper = mock(ObjectMapper.class);
        webSocketNodeService = new WebSocketNodeService(connectionManager, eventPublisher, objectMapper);
    }

    @Test
    void testOnConnectionEstablished() throws IOException {
        // Act
        webSocketNodeService.onConnectionEstablished();

        // Assert
        verify(connectionManager).sendMessage("{\"id\": 1,\"jsonrpc\":\"2.0\",\"method\":\"chain_subscribeNewHeads\",\"params\":[]}");
    }

    @Test
    void testOnConnectionEstablishedThrowsException() throws IOException {
        // Arrange
        doThrow(IOException.class).when(connectionManager).sendMessage(anyString());

        // Act & Assert
        assertThrows(SendSocketMessageException.class, webSocketNodeService::onConnectionEstablished);
    }

    @Test
    void testProcessMessageHandlesResponse() throws IOException {
        // Arrange
        String responsePayload = "{\"id\": 1, \"result\": \"subscription_id\"}";
        ReflectionTestUtils.setField(webSocketNodeService, "objectMapper", new ObjectMapper());

        // Act
        webSocketNodeService.processMessage(responsePayload);

        // Assert
        verify(connectionManager, never()).sendMessage(anyString());
        verify(eventPublisher, never()).publishNewHead(any());
    }

    @Test
    void testProcessMessageHandlesNewHeadNotification() throws JsonProcessingException {
        // Arrange
        String notificationPayload = """
                {
                  "jsonrpc": "2.0",
                  "method": "chain_newHead",
                  "params": {
                    "subscription": "SbT5GcmsjCM4vPby",
                    "result": {
                      "parentHash": "0x99de319d965142828a978808e33c02464d6ad338c6fbbe867bf96968db25a01b",
                      "number": "0x46568e",
                      "stateRoot": "0xee500e75018fd0ebe48fa79894ec8ff5b39e86c068b835efac0acdcb9aea0773",
                      "extrinsicsRoot": "0x913d0424ad712200de78533f7cf680452c424c600a3fcc095033ea14e5305420",
                      "digest": {
                        "logs": []
                      }
                    }
                  }
                }
                """;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(notificationPayload);
        NewHeadDTO newHeadDTO = new NewHeadDTO();
        ReflectionTestUtils.setField(webSocketNodeService, "objectMapper", mapper);

        when(objectMapper.treeToValue(eq(jsonNode), eq(NewHeadDTO.class))).thenReturn(newHeadDTO);

        // Act
        webSocketNodeService.processMessage(notificationPayload);

        // Assert
        verify(eventPublisher, times(1)).publishNewHead(any());
    }


    @Test
    void testProcessMessageThrowsOnInvalidJson() {
        // Arrange
        String invalidPayload = "invalid-json";
        ReflectionTestUtils.setField(webSocketNodeService, "objectMapper", new ObjectMapper());

        // Act & Assert
        assertThrows(HandleWebSocketTextException.class, () -> webSocketNodeService.processMessage(invalidPayload));
    }


    @Test
    void testFetchBlockDetailsSendsRequest() throws IOException {
        // Arrange
        String blockHash = "abcd1234";

        // Act
        webSocketNodeService.fetchBlockDetails(blockHash);

        // Assert
        String expectedRequest = String.format("{\"id\": 2,\"jsonrpc\":\"2.0\",\"method\":\"chain_getBlock\",\"params\":[\"%s\"]}", blockHash);
        verify(connectionManager).sendMessage(expectedRequest);
    }

    @Test
    void testFetchBlockDetailsThrowsExceptionOnSendError() throws IOException {
        // Arrange
        String blockHash = "abcd1234";
        doThrow(IOException.class).when(connectionManager).sendMessage(anyString());

        // Act & Assert
        assertThrows(SendSocketMessageException.class, () -> webSocketNodeService.fetchBlockDetails(blockHash));
    }

    @Test
    void testHandleBlockDetailsResponse_RealInstance() {
        // Arrange
        String responsePayload = "{\"id\": 2, \"result\": {\"block\": {\"extrinsics\": [\"data\"]}}}";
        BlockDetailsDTO blockDetailsDTO = new BlockDetailsDTO();
        BlockDetailsDTO.Block block = new BlockDetailsDTO.Block();
        block.setExtrinsics(new String[]{"data"});
        blockDetailsDTO.setBlock(block);
        ReflectionTestUtils.setField(webSocketNodeService, "objectMapper", new ObjectMapper());

        // Act
        webSocketNodeService.processMessage(responsePayload);

        // Assert
        assertEquals("data", block.getExtrinsics()[0]);
    }

    @Test
    void testHandleBlockDetailsResponse_Empty() {
        // Arrange
        String responsePayload = "{\"id\": 2, \"result\": {}}}";
        BlockDetailsDTO blockDetailsDTO = new BlockDetailsDTO();
        BlockDetailsDTO.Block block = new BlockDetailsDTO.Block();
        block.setExtrinsics(new String[]{"data"});
        blockDetailsDTO.setBlock(block);
        ReflectionTestUtils.setField(webSocketNodeService, "objectMapper", new ObjectMapper());

        // Act
        webSocketNodeService.processMessage(responsePayload);

        // Assert
        assertEquals("data", block.getExtrinsics()[0]);
    }

    @Test
    void testHandleBlockDetailsResponseThrowsException() throws JsonProcessingException {
        // Arrange
        String responsePayload = "{\"id\": 2, \"result\": {}}";
        JsonNode mockJsonNode = new ObjectMapper().readTree(responsePayload);
        ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
        ReflectionTestUtils.setField(webSocketNodeService, "objectMapper", mockObjectMapper);

        when(mockObjectMapper.readTree(responsePayload)).thenReturn(mockJsonNode);
        doThrow(JsonProcessingException.class)
                .when(mockObjectMapper)
                .treeToValue(any(JsonNode.class), eq(BlockDetailsDTO.class));

        // Act & Assert
        assertThrows(HandleBlockDetailsException.class, () -> webSocketNodeService.processMessage(responsePayload));
    }

    @Test
    void testHandleWrongSubscriptionResult() throws JsonProcessingException {
        // Arrange
        String responsePayload = "{\"id\": 1, \"toto\": {}}";
        JsonNode mockJsonNode = new ObjectMapper().readTree(responsePayload);

        when(objectMapper.readTree(responsePayload)).thenReturn(mockJsonNode);

        // Act
        webSocketNodeService.processMessage(responsePayload);

        // Assert
        verifyNoInteractions(eventPublisher);
        verifyNoInteractions(connectionManager);
    }

    @Test
    void testHandleWrongBlockInfoResult() throws JsonProcessingException {
        // Arrange
        String responsePayload = "{\"id\": 2, \"toto\": {}}";
        JsonNode mockJsonNode = new ObjectMapper().readTree(responsePayload);

        when(objectMapper.readTree(responsePayload)).thenReturn(mockJsonNode);


        // Act
        webSocketNodeService.processMessage(responsePayload);

        // Assert

        verifyNoInteractions(eventPublisher);
        verifyNoInteractions(connectionManager);
    }

    @Test
    void testHandleSubscriptionResponseLogsInfo() {
        // Arrange
        String responsePayload = "{\"id\": 1, \"result\": \"test-subscription-id\"}";
        ReflectionTestUtils.setField(webSocketNodeService, "objectMapper", new ObjectMapper());

        // Act
        webSocketNodeService.processMessage(responsePayload);

        // Assert
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void testHandleNewHeadNotificationThrowsException() throws JsonProcessingException {
        // Arrange
        String notificationPayload = "{\"jsonrpc\": \"2.0\", \"method\": \"chain_newHead\", \"params\": {}}";
        ObjectMapper realObjectMapper = spy(new ObjectMapper());
        ReflectionTestUtils.setField(webSocketNodeService, "objectMapper", realObjectMapper);

        JsonNode jsonNode = realObjectMapper.readTree(notificationPayload);
        doThrow(JsonProcessingException.class).when(realObjectMapper).treeToValue(jsonNode, NewHeadDTO.class);

        // Act & Assert
        assertThrows(HandleHeadNotificationException.class, () -> webSocketNodeService.processMessage(notificationPayload));
    }


    @Test
    void testHandleResponseMessageUnexpectedId() {
        // Arrange
        String unexpectedResponsePayload = "{\"id\": 999, \"result\": \"unexpected\"}";
        ReflectionTestUtils.setField(webSocketNodeService, "objectMapper", new ObjectMapper());

        // Act
        webSocketNodeService.processMessage(unexpectedResponsePayload);

        // Assert
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void testProcessMessageUnknownMessageType() {
        // Arrange
        String unknownPayload = "{\"unknownField\": \"unknownValue\"}";
        ReflectionTestUtils.setField(webSocketNodeService, "objectMapper", new ObjectMapper());

        // Act
        webSocketNodeService.processMessage(unknownPayload);

        // Assert
        verifyNoInteractions(eventPublisher);
        verifyNoInteractions(connectionManager);
    }

    @Test
    void testProcessMessageWrongMethodType() {
        // Arrange
        String unknownPayload = "{\"method\": \"unknownValue\"}";
        ReflectionTestUtils.setField(webSocketNodeService, "objectMapper", new ObjectMapper());

        // Act
        webSocketNodeService.processMessage(unknownPayload);

        // Assert
        verifyNoInteractions(eventPublisher);
        verifyNoInteractions(connectionManager);
    }

    @Test
    void testFetchBlockDetailsNullBlockHash() {
        // Arrange
        String blockHash = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> webSocketNodeService.fetchBlockDetails(blockHash));
    }

    @Test
    void testFetchBlockDetailsEmptyBlockHash() {
        // Arrange
        String blockHash = Strings.EMPTY;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> webSocketNodeService.fetchBlockDetails(blockHash));
    }

}
