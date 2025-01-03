package dev.cypherfury.juniscan.kafka;

import dev.cypherfury.juniscan.dto.NewHeadDTO;
import dev.cypherfury.juniscan.service.WebSocketNodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Service for processing new block head events received from Kafka.
 * <p>
 * This class listens to the Kafka topic defined in {@link KafkaPublisher#NEW_HEAD_TOPIC}
 * and processes {@link NewHeadDTO} messages by fetching the associated block details
 * from the blockchain node using {@link WebSocketNodeService}.
 * <p>
 * Responsibilities:
 * - Consume Kafka messages for new block heads.
 * - Delegate fetching of block details to {@link WebSocketNodeService}.
 * <p>
 * Dependencies:
 * - {@link WebSocketNodeService}: Interacts with the blockchain node to retrieve block details.
 *
 * @author Cypherfury
 */
@Service
@Slf4j
public class KafkaProcessor {

    private final WebSocketNodeService webSocketNodeService;

    /**
     * Constructs a new {@code NewHeadProcessor}.
     *
     * @param webSocketNodeService the service used to interact with the blockchain node.
     */
    public KafkaProcessor(WebSocketNodeService webSocketNodeService) {
        this.webSocketNodeService = webSocketNodeService;
    }

    /**
     * Listens to the Kafka topic for new block head events and processes them.
     * <p>
     * This method is invoked automatically whenever a new {@link NewHeadDTO} message
     * is published to the {@link KafkaPublisher#NEW_HEAD_TOPIC}.
     * It logs the incoming message and delegates the task of fetching block details
     * to the {@link WebSocketNodeService}.
     *
     * @param newHead the {@link NewHeadDTO} object representing the new block head.
     */
    @KafkaListener(topics = KafkaPublisher.NEW_HEAD_TOPIC, groupId = "chain-group")
    public void processNewHead(NewHeadDTO newHead) {
        log.info("Processing new head: {}", newHead);
        webSocketNodeService.fetchBlockDetails(newHead.getParams().getResult().getParentHash());
    }

}
