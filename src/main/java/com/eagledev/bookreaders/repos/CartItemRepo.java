package com.eagledev.bookreaders.repos;

import com.eagledev.bookreaders.entities.Cart;
import com.eagledev.bookreaders.entities.CartItem;
import com.eagledev.bookreaders.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepo extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndBook(Cart cart, Book book);
    List<CartItem> findByCart(Cart cart);
    void deleteAllByCart(Cart cart);
}

