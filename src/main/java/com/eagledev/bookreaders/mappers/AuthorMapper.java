package com.eagledev.bookreaders.mappers;

import com.eagledev.bookreaders.dtos.author.AuthorAdminResponse;
import com.eagledev.bookreaders.dtos.author.AuthorBooksResponse;
import com.eagledev.bookreaders.dtos.author.AuthorResponse;
import com.eagledev.bookreaders.entities.Author;
import com.eagledev.bookreaders.entities.Book;
import com.eagledev.bookreaders.entities.UserAuthor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorMapper {

    @Mapping(source = "followers", target = "followersCount", qualifiedByName = "countFollowers")
    @Mapping(source = "books", target = "booksCount", qualifiedByName = "countBooks")
    @Mapping(source = "books" , target = "books")
    AuthorResponse authorToAuthorResponse(Author author);

    @Mapping(source = "author", target = ".")
    AuthorResponse userAuthorToAuthorResponse(UserAuthor userAuthor);

    @Mapping(source = "followers", target = "followersCount", qualifiedByName = "countFollowers")
    @Mapping(source = "books" , target = "books")
    @Mapping(source = "books", target = "booksCount", qualifiedByName = "countBooks")
    AuthorAdminResponse authorToAuthorAdminResponse(Author author);

    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "edition", source = "edition")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "ebookCoverUrl", source = "ebookCoverUrl")
    AuthorBooksResponse booksToAuthorBookResponse(Book book);

    default List<AuthorResponse> authorsToAuthorResponses(List<Author> authors) {
        if (authors == null || authors.isEmpty()) {
            return new ArrayList<>();
        }
        return authors.stream()
                .map(this::authorToAuthorResponse)
                .collect(Collectors.toList());
    }

    default List<AuthorResponse> userAuthorsToAuthorResponses(List<UserAuthor> userAuthors) {
        if (userAuthors == null || userAuthors.isEmpty()) {
            return new ArrayList<>();
        }
        return userAuthors.stream()
                .map(this::userAuthorToAuthorResponse)
                .collect(Collectors.toList());
    }

    @Named("countFollowers")
    default Long countFollowers(List<UserAuthor> followers) {
        return followers == null || followers.isEmpty() ? 0L : (long) followers.size();
    }

    @Named("countBooks")
    default Long countBooks(List<Book> books) {
        return books == null || books.isEmpty() ? 0L : (long) books.size();
    }


}