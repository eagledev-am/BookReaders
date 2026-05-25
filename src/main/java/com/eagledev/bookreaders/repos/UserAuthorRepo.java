package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.Author;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.entities.UserAuthor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserAuthorRepo extends JpaRepository<UserAuthor,Integer> {
    boolean existsByUserAndAuthor(User user , Author author);
    void deleteByUserAndAuthor(User user, Author author);
    void deleteByAuthorId(int id);

    @EntityGraph(attributePaths = {"author" ,"author.books" , "author.books.discussionRoom"})
    List<UserAuthor> findByUserId(int userId);
}
