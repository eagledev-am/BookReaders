package com.eagledev.bookreaders.dtos.notification;

import com.eagledev.bookreaders.entities.enums.MailType;
import lombok.Builder;
import lombok.Getter;

@Builder
public record EmailNotification(
        String to,
        String name,
        String subject,
        String code,
        MailType Type
) {
}
