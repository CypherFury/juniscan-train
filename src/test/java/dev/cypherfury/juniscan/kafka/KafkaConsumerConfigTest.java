package dev.cypherfury.juniscan.kafka;

import dev.cypherfury.juniscan.dto.NewHeadDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link KafkaConsumerConfig} class.
 * <p>
 * Responsibilities:
 * - Validate the correct configuration of the Kafka ConsumerFactory.
 * - Ensure the KafkaListenerContainerFactory is properly configured.
 * - Test the initialization and behavior of the JsonDeserializer.
 * - Verify the correctness of configuration properties like server address and group ID.
 * These tests ensure the reliability and correctness of the Kafka consumer configuration.
 *
 * @author Cypherfury
 */
class KafkaConsumerConfigTest {

    @InjectMocks
    private KafkaConsumerConfig kafkaConsumerConfig;

    private final String serverAddress = "localhost:9092";
    private final String groupId = "test-group";

    @BeforeEach
    void setup() {
        kafkaConsumerConfig = new KafkaConsumerConfig(serverAddress, groupId);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsumerFactoryConfiguration() {
        // Arrange
        // (No setup needed beyond the default setup)

        // Act
        ConsumerFactory<String, NewHeadDTO> consumerFactory = kafkaConsumerConfig.consumerFactory();

        // Assert
        assertNotNull(consumerFactory);

        Map<String, Object> configs = consumerFactory.getConfigurationProperties();
        assertEquals(serverAddress, configs.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(groupId, configs.get(ConsumerConfig.GROUP_ID_CONFIG));
        assertEquals(StringDeserializer.class, configs.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        assertInstanceOf(JsonDeserializer.class, configs.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
    }

    @Test
    void testKafkaListenerContainerFactoryConfiguration() {
        // Arrange
        // (No setup needed beyond the default setup)

        // Act
        ConcurrentKafkaListenerContainerFactory<String, NewHeadDTO> factory = kafkaConsumerConfig.kafkaListenerContainerFactory();

        // Assert
        assertNotNull(factory);
        assertNotNull(factory.getConsumerFactory());
        assertEquals(DefaultKafkaConsumerFactory.class, factory.getConsumerFactory().getClass());
    }

    @Test
    void testJsonDeserializerInitialization() {
        // Arrange
        // (No setup needed beyond the default setup)

        // Act
        JsonDeserializer<NewHeadDTO> deserializer = new JsonDeserializer<>(NewHeadDTO.class);

        // Assert
        assertDoesNotThrow(() -> new JsonDeserializer<>(NewHeadDTO.class));
        assertNotNull(deserializer);
    }
}
