package dev.cypherfury.juniscan.kafka;

import dev.cypherfury.juniscan.dto.NewHeadDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for setting up Kafka consumers in the application.
 * <p>
 * This configuration enables Kafka in the application context and provides the required beans
 * to consume messages from a Kafka topic. The messages are deserialized into {@link NewHeadDTO}
 * objects for further processing.
 * <p>
 * Responsibilities:
 * - Configures the Kafka ConsumerFactory with custom deserialization for {@link NewHeadDTO}.
 * - Provides a ConcurrentKafkaListenerContainerFactory for handling Kafka listener methods.
 * <p>
 * Dependencies:
 * - The Kafka server address and group ID are injected via application properties.
 *
 * @author Cypherfury
 */
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    private final String serverAddress;
    private final String groupId;

    /**
     * Constructor to initialize KafkaConsumerConfig with server address and consumer group ID.
     *
     * @param serverAddress the address of the Kafka server, injected from application properties.
     * @param groupId       the Kafka consumer group ID, injected from application properties.
     */
    public KafkaConsumerConfig(@Value("${spring.kafka.bootstrap-servers}") String serverAddress,
                               @Value("${spring.kafka.consumer.group-id}") String groupId) {
        this.serverAddress = serverAddress;
        this.groupId = groupId;
    }

    /**
     * Creates a {@link ConsumerFactory} for consuming Kafka messages with {@link NewHeadDTO} as the value type.
     * <p>
     * The configuration includes:
     * - Custom deserialization for {@link NewHeadDTO}.
     * - Trusted package configuration for deserialization.
     * - Standard Kafka consumer configurations like server address and group ID.
     *
     * @return a configured {@link ConsumerFactory} instance.
     */
    @Bean
    public ConsumerFactory<String, NewHeadDTO> consumerFactory() {
        JsonDeserializer<NewHeadDTO> deserializer = new JsonDeserializer<>(NewHeadDTO.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddress);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    /**
     * Creates a {@link ConcurrentKafkaListenerContainerFactory} for Kafka listeners to consume messages asynchronously.
     * <p>
     * The factory uses the configured {@link ConsumerFactory} to handle message deserialization and processing.
     *
     * @return a configured {@link ConcurrentKafkaListenerContainerFactory} instance.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NewHeadDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NewHeadDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

}
