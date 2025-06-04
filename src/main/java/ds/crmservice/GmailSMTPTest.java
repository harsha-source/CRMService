package ds.crmservice;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;


public class GmailSMTPTest {
    public static void main(String[] args) {
        String host = "smtp.gmail.com";
        final String username = "ediss.hdurvasu@gmail.com";
        final String password = "xwgatlpjtwammrxb";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new jakarta.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(username));
            message.setSubject("SMTP Test");
            message.setText("If you received this, SMTP login works!");

            Transport.send(message);

            System.out.println("✅ Email sent successfully");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

