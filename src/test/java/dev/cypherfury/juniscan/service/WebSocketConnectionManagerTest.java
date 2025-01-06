package dev.cypherfury.juniscan.service;

import dev.cypherfury.juniscan.exception.HandleTransportException;
import dev.cypherfury.juniscan.exception.ReconnectWebSocketException;
import dev.cypherfury.juniscan.utils.Sleeper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebSocketConnectionManagerTest {

    private WebSocketConnectionManager manager;

    @Mock
    private WebSocketNodeService nodeService;

    @Mock
    private WebSocketSession session;

    @Mock
    private Sleeper sleeper;

    private static final String RPC_URL = "ws://localhost:8080/websocket";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        manager = new WebSocketConnectionManager(RPC_URL, nodeService, sleeper);
    }

    @Test
    void initializeConnection() {
        // Arrange

        // Act & Assert
        assertDoesNotThrow(() -> manager.initializeConnection());
    }

    @Test
    void tryToReconnect_throwsReconnectWebSocketException_whenInterrupted() throws InterruptedException {
        // Arrange
        doThrow(new InterruptedException("Mock InterruptedException"))
                .when(sleeper).sleep(anyLong());

        Thread testThread = new Thread(() -> manager.afterConnectionClosed(session, CloseStatus.SERVER_ERROR));

        // Act
        testThread.start();
        testThread.join();

        // Assert
        assertThrows(
                ReconnectWebSocketException.class,
                () -> {
                    throw new ReconnectWebSocketException(new InterruptedException("Mock InterruptedException"));
                }
        );
    }

    @Test
    void testAfterConnectionEstablished() {
        // Arrange

        // Act
        manager.afterConnectionEstablished(session);

        // Assert
        verify(nodeService).onConnectionEstablished();
        assertEquals(session, manager.getCurrentSession());
    }

    @Test
    void testHandleTextMessage() {
        // Arrange
        String payload = "{\"event\":\"test\"}";
        TextMessage textMessage = new TextMessage(payload);

        // Act
        manager.handleTextMessage(session, textMessage);

        // Assert
        verify(nodeService).processMessage(payload);
    }

    @Test
    void testHandleTransportError() {
        // Arrange
        Throwable exception = new RuntimeException("Transport error");

        // Act & Assert
        assertThrows(HandleTransportException.class, () ->
                manager.handleTransportError(session, exception));
    }

    @Test
    void testAfterConnectionClosed() {
        // Arrange
        CloseStatus status = new CloseStatus(1000, "Normal Closure");

        // Act
        manager.afterConnectionClosed(session, status);

        // Assert
        assertNull(manager.getCurrentSession());
        verify(nodeService, never()).onConnectionEstablished(); // No immediate reconnection in this method
    }

    @Test
    void testSendMessageWhenSessionIsOpen() throws IOException {
        // Arrange
        String message = "Test Message";
        when(session.isOpen()).thenReturn(true);
        manager.setCurrentSession(session);

        // Act
        manager.sendMessage(message);

        // Assert
        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session).sendMessage(messageCaptor.capture());
        assertEquals(message, messageCaptor.getValue().getPayload());
    }

    @Test
    void testSendMessageWhenSessionIsClosed() {
        // Arrange
        String message = "Test Message";
        when(session.isOpen()).thenReturn(false);
        manager.setCurrentSession(session);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> manager.sendMessage(message));
        assertEquals("WebSocket session is closed.", exception.getMessage());
    }

    @Test
    void afterConnectionEstablished_shouldThrowNullPointerException_whenSessionIsNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> manager.afterConnectionEstablished(null));
    }

    @Test
    void handleTextMessage_shouldThrowNullPointerException_whenSessionIsNull() {
        // Arrange
        TextMessage message = mock(TextMessage.class);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> manager.handleTextMessage(null, message));
    }

    @Test
    void handleTextMessage_shouldThrowNullPointerException_whenMessageIsNull() {
        // Arrange
        WebSocketSession session = mock(WebSocketSession.class);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> manager.handleTextMessage(session, null));
    }

    @Test
    void handleTransportError_shouldThrowNullPointerException_whenSessionIsNull() {
        // Arrange
        Throwable exception = mock(Throwable.class);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> manager.handleTransportError(null, exception));
    }

    @Test
    void handleTransportError_shouldThrowNullPointerException_whenExceptionIsNull() {
        // Arrange
        WebSocketSession session = mock(WebSocketSession.class);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> manager.handleTransportError(session, null));
    }

    @Test
    void afterConnectionClosed_shouldThrowNullPointerException_whenSessionIsNull() {
        // Arrange
        CloseStatus status = mock(CloseStatus.class);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> manager.afterConnectionClosed(null, status));
    }

    @Test
    void afterConnectionClosed_shouldThrowNullPointerException_whenStatusIsNull() {
        // Arrange
        WebSocketSession session = mock(WebSocketSession.class);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> manager.afterConnectionClosed(session, null));
    }

    @Test
    void sendMessage_shouldThrowIllegalStateException_whenCurrentSessionIsNull() {
        // Arrange
        String message = "Test message";
        manager.setCurrentSession(null);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> manager.sendMessage(message));
        assertEquals("WebSocket session is closed.", exception.getMessage());
    }

    @Test
    void sendMessage_shouldThrowIllegalStateException_whenCurrentSessionIsNotOpen() {
        // Arrange
        String message = "Test message";
        WebSocketSession sessionMock = mock(WebSocketSession.class);
        when(sessionMock.isOpen()).thenReturn(false);
        manager.setCurrentSession(sessionMock);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> manager.sendMessage(message));
        assertEquals("WebSocket session is closed.", exception.getMessage());
    }


}
