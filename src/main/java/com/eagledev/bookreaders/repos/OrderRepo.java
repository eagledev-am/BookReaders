package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepo extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"items", "items.book", "user"})
    Optional<Order> findByUuid(UUID uuid);

    @EntityGraph(attributePaths = {"items", "items.book","items.book.discussionRoom", "user"})
    Page<Order> findAllByUserUuid(UUID userUuid, Pageable pageable);

    @EntityGraph(attributePaths = {"items", "items.book","items.book.discussionRoom", "user"})
    Page<Order> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"items", "items.book", "user"})
    List<Order> findAllByUserUuid(UUID userUuid);


    @EntityGraph(attributePaths = {"items", "items.book", "user"})
    @Query(
            "SELECT o FROM Order o WHERE LOWER(o.user.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
                    "OR LOWER(o.user.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Order> searchByUser(@Param("query") String query,
                                                             Pageable pageable);
}
