package com.eagledev.bookreaders.mappers;

import com.eagledev.bookreaders.dtos.book.*;
import com.eagledev.bookreaders.entities.Author;
import com.eagledev.bookreaders.entities.Book;
import com.eagledev.bookreaders.entities.Category;
import com.eagledev.bookreaders.entities.DiscussionRoom;
import com.eagledev.bookreaders.entities.UserRating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = AuthorMapper.class)
public interface BookMapper {

    @Mapping(source = "ratings", target = "averageRating", qualifiedByName = "calculateAverageRating")
    @Mapping(source = "authors", target = "authors")
    BookPageResponse toPageResponse(Book book);

    @Mapping(source = "authors", target = "authors")
    BookPageAdminResponse toBookPageAdminResponse(Book book);

    @Mapping(source = "categories", target = "categories", qualifiedByName = "mapCategoriesTag")
    @Mapping(source = "authors", target = "authors")
    @Mapping(source = "ratings", target = "averageRating", qualifiedByName = "calculateAverageRating")
    @Mapping(source = "discussionRoom", target = "discussionRoomUuid", qualifiedByName = "getRoomUuid")
    BookResponse bookToBookResponse(Book book);

    @Mapping(source = "categories", target = "categories", qualifiedByName = "mapCategoriesTag")
    @Mapping(source = "authors", target = "authors")
    @Mapping(source = "ratings", target = "averageRating", qualifiedByName = "calculateAverageRating")
    @Mapping(source = "discussionRoom", target = "discussionRoomUuid", qualifiedByName = "getRoomUuid")
    BookAdminResponse bookToBookAdminResponse(Book book);

    BookAuthorResponse authorToAuthorBookResponse(Author author);

    @Named("mapCategoriesTag")
    default List<String> mapCategoriesTag(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return List.of();
        }
        return categories.stream()
                .map(Category::getTag)
                .collect(Collectors.toList());
    }


    @Named("calculateAverageRating")
    default Double calculateAverageRating(List<UserRating> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToDouble(UserRating::getScore)
                .average()
                .orElse(0.0);
    }


    @Named("getRoomUuid")
    default UUID getRoomUuid(DiscussionRoom discussionRoom) {
        if (discussionRoom == null) {
            return null;
        }
        return discussionRoom.getUuid();
    }


    default List<BookResponse> booksToBookResponses(List<Book> books) {
        if (books == null || books.isEmpty()) {
            return List.of();
        }
        return books.stream()
                .map(this::bookToBookResponse)
                .collect(Collectors.toList());
    }
}