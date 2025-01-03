package dev.cypherfury.juniscan.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cypherfury.juniscan.dto.BlockDetailsDTO;
import dev.cypherfury.juniscan.dto.NewHeadDTO;
import dev.cypherfury.juniscan.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

/**
 * Handles business logic related to blockchain events received via WebSocket.
 * <p>
 * Responsibilities:
 * - Process messages from the WebSocket.
 * - Publish relevant events to Kafka.
 * - Handle JSON-RPC requests and responses.
 *
 *  @author Cypherfury
 */
@Service
@Slf4j
public class WebSocketNodeService {

    private static final String SUBSCRIPTION_REQUEST = "{\"id\": 1,\"jsonrpc\":\"2.0\",\"method\":\"chain_subscribeNewHeads\",\"params\":[]}";
    private static final String FETCH_BLOCK_REQUEST = "{\"id\": 2,\"jsonrpc\":\"2.0\",\"method\":\"chain_getBlock\",\"params\":[\"%s\"]}";

    private static final String NEW_HEAD_FIELD = "chain_newHead";
    private static final String METHOD_FIELD = "method";
    private static final String RESULT_FIELD = "result";
    private static final String ID_FIELD = "id";

    private static final int BLOCK_DETAILS_ID = 2;
    private static final int SUBSCRIPTION_ID = 1;

    private final WebSocketConnectionManager connectionManager;
    private final KafkaEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    /**
     * Constructor to initialize the service with required dependencies.
     *
     * @param eventPublisher    Kafka event publisher for blockchain events.
     * @param connectionManager Manages WebSocket connections and message sending.
     * @param objectMapper      JSON parser and serializer.
     */
    public WebSocketNodeService(WebSocketConnectionManager connectionManager,
                                KafkaEventPublisher eventPublisher,
                                ObjectMapper objectMapper) {
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
        this.connectionManager = connectionManager;
    }

    /**
     * Sends the subscription request after the connection is established.
     *
     * @throws SendSocketMessageException If an error occurs while sending the subscription request.
     */
    public void onConnectionEstablished() {
        try {
            log.info("Sending subscription request...");
            connectionManager.sendMessage(SUBSCRIPTION_REQUEST);
        } catch (IOException e) {
            log.error("Error during subscription: {}", e.getMessage());
            throw new SendSocketMessageException(SUBSCRIPTION_REQUEST, e);
        }
    }

    /**
     * Processes incoming messages from the WebSocket.
     *
     * @param payload The raw JSON message payload.
     * @throws HandleWebSocketTextException If an error occurs while parsing the JSON payload.
     */
    public void processMessage(String payload) {
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            if (jsonNode.has(ID_FIELD)) {
                handleResponseMessage(jsonNode);
            } else if (jsonNode.has(METHOD_FIELD) && NEW_HEAD_FIELD.equals(jsonNode.get(METHOD_FIELD).asText())) {
                handleNewHeadNotification(jsonNode);
            } else {
                log.warn("Unknown message type received: {}", payload);
            }
        } catch (JsonProcessingException e) {
            throw new HandleWebSocketTextException(payload, e);
        }
    }

    /**
     * Handles response messages based on their ID and processes corresponding data.
     *
     * @param jsonNode The JSON node representing the response message.
     */
    private void handleResponseMessage(JsonNode jsonNode) {
        int id = jsonNode.get(ID_FIELD).asInt();
        if (id == SUBSCRIPTION_ID && jsonNode.has(RESULT_FIELD)) {
            handleSubscriptionResponse(jsonNode);
        } else if (id == BLOCK_DETAILS_ID && jsonNode.has(RESULT_FIELD)) {
            handleBlockDetailsResponse(jsonNode);
        } else {
            log.warn("Unexpected response received: {}", jsonNode);
        }
    }

    /**
     * Handles the subscription response received from the WebSocket.
     *
     * @param jsonNode The JSON node representing the subscription response.
     */
    private void handleSubscriptionResponse(JsonNode jsonNode) {
        String subscriptionId = jsonNode.get(RESULT_FIELD).asText();
        log.info("Subscription successful. Subscription ID: {}", subscriptionId);
    }

    /**
     * Processes block details from the given JSON response.
     *
     * @param jsonNode The JSON node containing block details.
     * @throws HandleBlockDetailsException If an error occurs while processing block details.
     */
    private void handleBlockDetailsResponse(JsonNode jsonNode) {
        try {
            BlockDetailsDTO blockDetails = objectMapper.treeToValue(jsonNode.get(RESULT_FIELD), BlockDetailsDTO.class);
            log.info("Processing block details: {}", blockDetails);
            if (blockDetails.getBlock() != null) {
                log.info("Extrinsics: {}", Arrays.toString(blockDetails.getBlock().getExtrinsics()));
                // TODO: Add block service to analyze, decode & save in DB
            } else {
                log.warn("Block details are null for the provided block.");
            }
        } catch (JsonProcessingException e) {
            throw new HandleBlockDetailsException(jsonNode, e);
        }
    }

    /**
     * Processes a new head notification received from the WebSocket.
     *
     * @param jsonNode The JSON node containing the new head notification.
     * @throws HandleHeadNotificationException If an error occurs while processing the head notification.
     */
    private void handleNewHeadNotification(JsonNode jsonNode) {
        try {
            NewHeadDTO newHeadMessage = objectMapper.treeToValue(jsonNode, NewHeadDTO.class);
            eventPublisher.publishNewHead(newHeadMessage);
        } catch (JsonProcessingException e) {
            throw new HandleHeadNotificationException(jsonNode, e);
        }
    }

    /**
     * Fetches details of a block using its hash by sending a JSON-RPC request to the WebSocket.
     *
     * @param blockHash The hash of the block whose details are to be fetched.
     * @throws IllegalArgumentException If the block hash is null or empty.
     * @throws SendSocketMessageException If an error occurs while sending the fetch request.
     */
    public void fetchBlockDetails(String blockHash) {
        if (blockHash == null || blockHash.isEmpty()) {
            throw new IllegalArgumentException("Block hash must not be null or empty");
        }
        try {
            String request = String.format(FETCH_BLOCK_REQUEST, blockHash);
            connectionManager.sendMessage(request);
        } catch (IOException e) {
            throw new SendSocketMessageException(blockHash, e);
        }
    }
}
