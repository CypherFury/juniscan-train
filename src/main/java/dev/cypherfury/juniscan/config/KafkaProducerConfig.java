package dev.cypherfury.juniscan.config;

import dev.cypherfury.juniscan.dto.NewHeadDTO;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for setting up Kafka producers in the application.
 * <p>
 * This configuration provides the necessary beans to produce messages to a Kafka topic.
 * The messages are serialized into JSON format, using {@link JsonSerializer} for values
 * and {@link StringSerializer} for keys.
 * <p>
 * Responsibilities:
 * - Configures the Kafka ProducerFactory with JSON serialization for {@link NewHeadDTO}.
 * - Provides a KafkaTemplate for sending messages to Kafka topics.
 * <p>
 * Dependencies:
 * - The Kafka server address is injected via application properties.
 * <p>
 * @author Cypherfury
 */
@Configuration
public class KafkaProducerConfig {

    private final String serverAddress;

    /**
     * Constructor to initialize KafkaProducerConfig with the server address.
     *
     * @param serverAddress the address of the Kafka server, injected from application properties.
     */
    public KafkaProducerConfig(@Value("${spring.kafka.bootstrap-servers}") String serverAddress) {
        this.serverAddress = serverAddress;
    }

    /**
     * Creates a {@link ProducerFactory} for producing Kafka messages with {@link NewHeadDTO} as the value type.
     * <p>
     * The configuration includes:
     * - JSON serialization for values.
     * - String serialization for keys.
     * - Kafka server address.
     *
     * @return a configured {@link ProducerFactory} instance.
     */
    @Bean
    public ProducerFactory<String, NewHeadDTO> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddress);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * Creates a {@link KafkaTemplate} for sending messages to Kafka topics.
     * <p>
     * The template uses the configured {@link ProducerFactory} for serializing and sending messages.
     *
     * @return a configured {@link KafkaTemplate} instance.
     */
    @Bean
    public KafkaTemplate<String, NewHeadDTO> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}