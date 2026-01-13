package com.eagledev.bookreaders.dtos.book;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
public class BookAuthorResponse {
    UUID uuid;
    String name;
    String photoUrl;
}
