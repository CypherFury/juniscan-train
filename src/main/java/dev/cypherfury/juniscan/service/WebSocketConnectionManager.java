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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages the WebSocket connection to the blockchain node.
 * <p>
 * Responsibilities:
 * - Establish and maintain the WebSocket connection.
 * - Forward incoming messages to {@link WebSocketNodeService} for processing.
 * - Handle WebSocket lifecycle events and errors.
 * <p>
 * Improvements:
 * - Prevents multiple threads from attempting reconnection simultaneously.
 * - Uses {@link ScheduledExecutorService} for reconnection attempts to ensure proper thread management.
 * - Ensures a single active WebSocket connection at all times.
 * <p>
 * Dependencies:
 * - {@link Sleeper} for introducing delays between reconnection attempts.
 * - {@link WebSocketNodeService} for handling blockchain events.
 * <p>
 * Usage:
 * - Automatically initialized by Spring via the `@Service` annotation.
 * - Manages connection lifecycle and reestablishes connection if interrupted.
 *
 * @author Cypherfury
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
    private final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor();

    private final Object lock = new Object();

    /**
     * Constructor to initialize the WebSocketConnectionManager.
     *
     * @param rpcUrl     The WebSocket URL of the blockchain node.
     * @param nodeService The service that processes blockchain events.
     * @param sleeper    Utility for introducing delays in reconnection attempts.
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
        if (currentSession != null && currentSession.isOpen()) {
            log.info("WebSocket session already open. Skipping new connection.");
            return;
        }
        log.info("Initializing WebSocket connection...");
        connect();
    }

    /**
     * Establishes a new WebSocket connection.
     */
    private void connect() {
        synchronized (lock) {
            try {
                log.info("Attempting to open WebSocket connection...");
                StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
                webSocketClient.execute(this, rpcUrl);
            } catch (Exception e) {
                log.error("Failed to establish WebSocket connection: {}", e.getMessage());
                scheduleReconnect();
            }
        }
    }

    /**
     * Called when the WebSocket connection is established.
     * Delegates subscription handling to {@link WebSocketNodeService}.
     */
    @Override
    public synchronized void afterConnectionEstablished(@NonNull WebSocketSession session) {
        log.info("WebSocket connection established.");
        currentSession = session;
        nodeService.onConnectionEstablished();
    }

    /**
     * Handles incoming WebSocket messages and forwards them to {@link WebSocketNodeService} for processing.
     *
     * @param session The active WebSocket session.
     * @param message The incoming message.
     */
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        log.info("Message received: {}", payload);
        nodeService.processMessage(payload);
    }

    /**
     * Handles transport-level errors during WebSocket communication.
     *
     * @param session   The active WebSocket session.
     * @param exception The exception that occurred.
     */
    @Override
    public void handleTransportError(@NonNull WebSocketSession session, Throwable exception) {
        log.error("WebSocket transport error: {}", exception.getMessage());
        throw new HandleTransportException(session, exception);
    }

    /**
     * Called when the WebSocket connection is closed.
     * Logs the reason and triggers a reconnection attempt.
     *
     * @param session The closed WebSocket session.
     * @param status  The close status.
     */
    @Override
    public synchronized void afterConnectionClosed(@NonNull WebSocketSession session, CloseStatus status) {
        log.info("WebSocket connection closed: {}", status.getReason());
        currentSession = null; // Reset the session
        scheduleReconnect();
    }

    /**
     * Schedules a reconnection attempt to the WebSocket server.
     */
    private void scheduleReconnect() {
        reconnectExecutor.schedule(this::connect, RECONNECT_DELAY_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * Sends a message through the current WebSocket session.
     *
     * @param message The JSON-RPC message to send.
     * @throws IOException if there is an error sending the message.
     */
    public synchronized void sendMessage(String message) throws IOException {
        if (currentSession != null && currentSession.isOpen()) {
            currentSession.sendMessage(new TextMessage(message));
        } else {
            log.error("WebSocket session is not open.");
            throw new IllegalStateException("WebSocket session is closed.");
        }
    }
}
