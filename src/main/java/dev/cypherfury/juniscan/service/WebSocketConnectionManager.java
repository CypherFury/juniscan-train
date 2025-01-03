package dev.cypherfury.juniscan.service;

import dev.cypherfury.juniscan.exception.HandleTransportException;
import dev.cypherfury.juniscan.exception.ReconnectWebSocketException;
import dev.cypherfury.juniscan.utils.Sleeper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

/**
 * Manages the WebSocket connection to the blockchain node.
 * <p>
 * Responsibilities:
 * - Establish and maintain the WebSocket connection.
 * - Forward incoming messages to WebSocketNodeService for processing.
 * - Handle WebSocket lifecycle events and errors.
 *
 *  @author Cypherfury
 */
@Service
@Slf4j
public class WebSocketConnectionManager extends TextWebSocketHandler {

    private final WebSocketNodeService nodeService;
    private final Sleeper sleeper;
    private final String rpcUrl;

    @Setter
    @Getter
    private WebSocketSession currentSession;

    private static final int RECONNECT_DELAY_MS = 5000;

    /**
     * Constructor to initialize the WebSocketConnectionManager.
     *
     * @param rpcUrl     The WebSocket URL of the blockchain node.
     * @param nodeService The service that processes blockchain events.
     */
    public WebSocketConnectionManager(@Value("${rpc.url}") String rpcUrl,
                                      @Lazy WebSocketNodeService nodeService,
                                      Sleeper sleeper) {
        this.nodeService = nodeService;
        this.sleeper = sleeper;
        this.rpcUrl = rpcUrl;
    }

    /**
     * Initializes the WebSocket connection after the service is created.
     * This method is invoked automatically by the Spring framework.
     */
    @PostConstruct
    public void initializeConnection() {
        log.info("Initializing WebSocket connection...");
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        webSocketClient.execute(this, rpcUrl);
    }

    /**
     * Called when the WebSocket connection is established.
     * Delegates subscription handling to WebSocketNodeService.
     */
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        log.info("WebSocket connection established.");
        currentSession = session;
        nodeService.onConnectionEstablished();
    }

    /**
     * Handles incoming WebSocket messages and forwards them to WebSocketNodeService for processing.
     */
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        log.info("Message received: {}", payload);
        nodeService.processMessage(payload);
    }

    /**
     * Handles transport-level errors during WebSocket communication.
     */
    @Override
    public void handleTransportError(@NonNull WebSocketSession session, Throwable exception) {
        log.error("WebSocket transport error: {}", exception.getMessage());
        throw new HandleTransportException(session, exception);
    }

    /**
     * Called when the WebSocket connection is closed.
     * Logs the reason and optionally triggers a reconnection attempt.
     */
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, CloseStatus status) {
        log.info("WebSocket connection closed: {}", status.getReason());
        currentSession = null;
        retryConnection();
    }

    /**
     * Sends a message through the current WebSocket session.
     *
     * @param message The JSON-RPC message to send.
     * @throws IOException if there is an error sending the message.
     */
    public void sendMessage(String message) throws IOException {
        if (currentSession != null && currentSession.isOpen()) {
            currentSession.sendMessage(new TextMessage(message));
        } else {
            log.error("WebSocket session is not open.");
            throw new IllegalStateException("WebSocket session is closed.");
        }
    }

    /**
     * Initiates a reconnection attempt to the WebSocket server.
     */
    private void retryConnection() {
        new Thread(() -> {
            while (currentSession == null) {
                tryToReconnect();
            }
        }).start();
    }

    /**
     * Tries to reconnect to the WebSocket server with a delay between attempts.
     */
    private void tryToReconnect() {
        try {
            log.info("Attempting to reconnect to WebSocket...");
            sleeper.sleep(RECONNECT_DELAY_MS);
            StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
            webSocketClient.execute(this, rpcUrl);
        } catch (InterruptedException e) {
            log.error("Reconnection attempt failed: {}", e.getMessage());
            throw new ReconnectWebSocketException(e);
        }
    }

}
