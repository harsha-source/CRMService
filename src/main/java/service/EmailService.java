package service;

import dto.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${andrew.id}")
    private String andrewId;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostConstruct
    public void init() {
        log.info(" EmailService initialized. Using sender address from configuration.");
    }

    /**
     * Sends a welcome email to the newly registered customer
     *
     * @param customer the newly registered customer
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendWelcomeEmail(CustomerDTO customer) {
        log.debug(" Preparing welcome email for customer: {}", customer);

        if (customer == null) {
            log.warn(" Attempted to send email, but received null customer object.");
            return false;
        }

        if (customer.getUserId() == null || customer.getUserId() == null) {
            log.warn("Missing required customer fields. userId={}, email={}",
                    customer.getUserId(), customer.getUserId());
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(customer.getUserId());
            message.setSubject("Activate your book store account");
            message.setText(
                    "Dear " + customer.getName() + ",\n\n" +
                            "Welcome to the Book store created by " + andrewId + ".\n" +
                            "Exceptionally this time we won't ask you to click a link to activate your account."
            );

            log.info(" Sending welcome email to: {} ({})", customer.getName(), customer.getUserId());

            mailSender.send(message);

            log.info(" Welcome email successfully sent to: {} ({})",
                    customer.getName(), customer.getUserId());
            return true;

        } catch (Exception e) {
            log.error(" Failed to send welcome email to: {} ({})",
                    customer.getName(), customer.getUserId(), e);
            return false;
        }
    }
}
