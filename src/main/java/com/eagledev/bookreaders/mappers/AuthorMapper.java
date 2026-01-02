package com.eagledev.bookreaders.mappers;

import com.eagledev.bookreaders.dtos.author.AuthorResponse;
import com.eagledev.bookreaders.entities.Author;
import com.eagledev.bookreaders.entities.UserAuthor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorMapper {


    @Mapping(source = "author.uuid", target = "uuid")
    @Mapping(source = "author.name", target = "name")
    @Mapping(source = "author.bio", target = "bio")
    @Mapping(source = "author.dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "author.photoUrl", target = "photoUrl")
    @Mapping(source = "author.nationality", target = "nationality")
    AuthorResponse userAuthorToAuthorResponse(UserAuthor userAuthor);

}
