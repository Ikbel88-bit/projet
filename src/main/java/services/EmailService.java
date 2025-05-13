package services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;
import java.util.Random;

public class EmailService {
    private JavaMailSender mailSender;
    
    public EmailService() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("mohamedeabidi1@gmail.com");
        mailSender.setPassword("sikk cqjr drhw trfy");
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        
        this.mailSender = mailSender;
    }
    
    public void sendVerificationCode(String to, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("mohamedeabidi1@gmail.com");
            message.setTo(to);
            message.setSubject("Code de vérification - Inscription");
            message.setText("Votre code de vérification est: " + code);
            
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
    }
    
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 1000 + random.nextInt(9000); // Génère un nombre entre 1000 et 9999
        return String.valueOf(code);
    }
}