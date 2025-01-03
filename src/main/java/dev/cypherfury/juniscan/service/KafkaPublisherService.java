package dev.cypherfury.juniscan.service;

import dev.cypherfury.juniscan.dto.NewHeadDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for publishing events to Kafka topics.
 * <p>
 * This class is responsible for sending {@link NewHeadDTO} messages to a specific Kafka topic.
 * It encapsulates the interaction with the KafkaTemplate, ensuring that messages are sent
 * reliably and providing logging for debugging and monitoring.
 * <p>
 * Dependencies:
 * - {@link KafkaTemplate}: Handles the serialization and delivery of messages to Kafka.
 *
 * @author Cypherfury
 */
@Slf4j
@Service
public class KafkaPublisherService {

    /**
     * The Kafka topic used for publishing new block head events.
     */
    public static final String NEW_HEAD_TOPIC = "chain-new-head";

    private final KafkaTemplate<String, NewHeadDTO> kafkaTemplate;

    /**
     * Constructs a new {@code KafkaEventPublisher}.
     *
     * @param kafkaTemplate the {@link KafkaTemplate} used to send messages to Kafka.
     */
    public KafkaPublisherService(KafkaTemplate<String, NewHeadDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a new block head event to the Kafka topic.
     * <p>
     * The method serializes the provided {@link NewHeadDTO} and sends it to the
     * {@link #NEW_HEAD_TOPIC}. A log entry is created to confirm the publication.
     *
     * @param newHead the {@link NewHeadDTO} object representing the new block head.
     */
    public void publishNewHead(NewHeadDTO newHead) {
        kafkaTemplate.send(NEW_HEAD_TOPIC, newHead);
        log.info("Published new head to Kafka: {}", newHead);
    }

}
