package com.eagledev.bookreaders.dtos.user;

import com.eagledev.bookreaders.entities.enums.ContactType;
import com.eagledev.bookreaders.util.contacttype.ValidContact;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@ValidContact
public class UserContactDto {
    @NotBlank(message = "contact type is required.")
    private String contactType;
    @NotBlank(message = "contact value is required.")
    private String contactValue;
}
