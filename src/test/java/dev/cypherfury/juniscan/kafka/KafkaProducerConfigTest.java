package dev.cypherfury.juniscan.kafka;

import dev.cypherfury.juniscan.dto.NewHeadDTO;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link KafkaProducerConfig} class.
 * <p>
 * Responsibilities:
 * - Validate the correct configuration of the Kafka ProducerFactory.
 * - Ensure the KafkaTemplate is properly initialized.
 * - Verify the correctness of configuration properties like server address.
 * These tests ensure the reliability and correctness of the Kafka producer configuration.
 *
 * @author Cypherfury
 */
class KafkaProducerConfigTest {

    @InjectMocks
    private KafkaProducerConfig kafkaProducerConfig;

    private final String serverAddress = "localhost:9092";

    @BeforeEach
    void setup() {
        kafkaProducerConfig = new KafkaProducerConfig(serverAddress);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProducerFactoryConfiguration() {
        // Arrange
        // (No setup needed beyond the default setup)

        // Act
        ProducerFactory<String, NewHeadDTO> producerFactory = kafkaProducerConfig.producerFactory();

        // Assert
        assertNotNull(producerFactory);

        Map<String, Object> configs = producerFactory.getConfigurationProperties();
        assertEquals(serverAddress, configs.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(StringSerializer.class, configs.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(JsonSerializer.class, configs.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
    }

    @Test
    void testKafkaTemplateInitialization() {
        // Arrange
        // (No setup needed beyond the default setup)

        // Act
        KafkaTemplate<String, NewHeadDTO> kafkaTemplate = kafkaProducerConfig.kafkaTemplate();

        // Assert
        assertNotNull(kafkaTemplate);
        assertNotNull(kafkaTemplate.getProducerFactory());
        assertEquals(DefaultKafkaProducerFactory.class, kafkaTemplate.getProducerFactory().getClass());
    }
}
