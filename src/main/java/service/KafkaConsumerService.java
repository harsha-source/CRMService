package service;

import dto.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
@Slf4j
public class KafkaConsumerService {

    private final EmailService emailService;

    public KafkaConsumerService(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostConstruct
    public void init() {
        log.info(" KafkaConsumerService initialized. Awaiting customer events from Kafka...");
    }

    /**
     * Listens for customer registration events from Kafka
     * Processes the event and sends a welcome email to the customer
     *
     * @param customer the customer data from Kafka
     * @param acknowledgment manual acknowledgment to confirm message processing
     */
    @KafkaListener(topics = "${kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeCustomerRegistrationEvent(CustomerDTO customer, Acknowledgment acknowledgment) {
        log.debug(" Kafka listener triggered. Raw event received.");

        try {
            if (customer == null) {
                log.error(" Received null customer object from Kafka.");
                acknowledgment.acknowledge();
                return;
            }

            log.info("📦 Deserialized Kafka message: {}", customer);

            if (customer.getUserId() == null || customer.getUserId() == null) {
                log.warn(" Incomplete customer data: userId={}, email={}",
                        customer.getUserId(), customer.getUserId());
                acknowledgment.acknowledge();
                return;
            }

            log.info(" Attempting to send welcome email to userId={}, email={}",
                    customer.getUserId(), customer.getUserId());

            boolean emailSent = emailService.sendWelcomeEmail(customer);

            if (emailSent) {
                log.info(" Welcome email sent to: {} ({})",
                        customer.getUserId(), customer.getUserId());
            } else {
                log.warn(" Email service failed for userId={}, email={}. Still acknowledging.",
                        customer.getUserId(), customer.getUserId());
            }

            acknowledgment.acknowledge();
            log.debug(" Kafka message acknowledged for userId={}", customer.getUserId());

        } catch (Exception e) {
            log.error(" Unexpected error while processing customer message: {}", customer, e);
            // Message will be retried based on retry policy
            throw e;
        }
    }
}
