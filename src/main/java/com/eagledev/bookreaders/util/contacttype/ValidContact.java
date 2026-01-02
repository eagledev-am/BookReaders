package com.eagledev.bookreaders.util.contacttype;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ContactValidator.class) // Links to the logic class
@Target({ ElementType.TYPE , ElementType.FIELD}) // Applies to the Class, not just one field
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidContact {
    String message() default "Invalid contact URL or Value for the selected type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
