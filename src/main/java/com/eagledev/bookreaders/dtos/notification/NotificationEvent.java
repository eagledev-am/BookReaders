package com.eagledev.bookreaders.dtos.notification;

import com.eagledev.bookreaders.entities.enums.NotificationType;

import java.util.UUID;

public record NotificationEvent(
        UUID recipientUuid,
        String recipientEmail,
        String subject,
        String message,
        NotificationType type
) {
}
