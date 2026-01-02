package com.eagledev.bookreaders.util.contacttype;

import com.eagledev.bookreaders.dtos.user.UserContactDto;
import com.eagledev.bookreaders.entities.enums.ContactType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ContactValidator implements ConstraintValidator<ValidContact, UserContactDto> {

    private static final Pattern FACEBOOK_PATTERN = Pattern.compile("https?:\\/\\/(www\\.)?facebook\\.com\\/.+");
    private static final Pattern LINKEDIN_PATTERN = Pattern.compile("https?:\\/\\/(www\\.)?linkedin\\.com\\/in\\/.+");
    private static final Pattern X_PATTERN = Pattern.compile("https?:\\/\\/(www\\.)?(twitter\\.com|x\\.com)\\/.+");

    @Override
    public boolean isValid(UserContactDto dto, ConstraintValidatorContext constraintValidatorContext) {
        if(dto == null) return true;

        if(dto.getContactType() == null || dto.getContactValue() == null){
            return false;
        }

        try {
            ContactType type = ContactType.valueOf(dto.getContactType().toUpperCase());
            String url = dto.getContactValue();

            return switch (type) {
                case FACEBOOK -> FACEBOOK_PATTERN.matcher(url).matches();
                case LINKEDIN -> LINKEDIN_PATTERN.matcher(url).matches();
                case X -> X_PATTERN.matcher(url).matches();
            };

        } catch (IllegalArgumentException e) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Invalid Contact Type. Allowed: X, FACEBOOK, LINKEDIN")
                    .addPropertyNode("type")
                    .addConstraintViolation();
            return false;
        }
    }
}
