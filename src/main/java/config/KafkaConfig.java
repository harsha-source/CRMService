package config;

import dto.CustomerDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka configuration class for setting up Kafka consumers.
 * This class configures a Kafka consumer to receive CustomerDTO objects from Kafka topics.
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    // Comma-separated list of all Kafka broker addresses
    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Group ID for the Kafka consumer. Defaults to "crm-service" if not specified in properties.
     * Consumers with the same group ID will be part of the same consumer group.
     */
    @Value("${spring.kafka.consumer.group-id:crm-service}")
    private String groupId;

    /**
     * Creates and configures a Kafka consumer factory for CustomerDTO messages.
     * This factory is responsible for creating consumer instances.
     *
     * @return A configured ConsumerFactory for String keys and CustomerDTO values
     */
    @Bean
    public ConsumerFactory<String, CustomerDTO> consumerFactory() {
        JsonDeserializer<CustomerDTO> deserializer = new JsonDeserializer<>(CustomerDTO.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer.getClass());

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    /**
     * Creates and configures a Kafka listener container factory.
     * This factory creates containers for Kafka message listeners with error handling and acknowledgment settings.
     *
     * @return A configured ConcurrentKafkaListenerContainerFactory for handling CustomerDTO messages
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CustomerDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CustomerDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        // Configure error handling with retry logic: 3 retries with 1 second between attempts
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(new FixedBackOff(1000L, 3));
        factory.setCommonErrorHandler(errorHandler);
        // Set to manual acknowledgment mode for better control over message processing
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }
}