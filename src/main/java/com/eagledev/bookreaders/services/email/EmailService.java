package com.eagledev.bookreaders.services.email;

import com.eagledev.bookreaders.dtos.notification.EmailNotification;
import com.eagledev.bookreaders.entities.enums.MailType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(EmailNotification notification) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", notification.name());
        context.setVariable("code", notification.code());

        String emailBody =(notification.Type() == MailType.RESET_PASSWORD_MAIL)?
                templateEngine.process("reset-password", context) :
                templateEngine.process("verification-email", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(notification.to());
        helper.setSubject(notification.subject());
        helper.setText(emailBody, true);

        mailSender.send(message);
    }
}
