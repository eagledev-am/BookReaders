package com.eagledev.bookreaders.config;

import com.eagledev.bookreaders.entities.Author;
import com.eagledev.bookreaders.entities.Book;
import com.eagledev.bookreaders.entities.Category;
import com.eagledev.bookreaders.entities.DiscussionRoom;
import com.eagledev.bookreaders.repos.AuthorRepo;
import com.eagledev.bookreaders.repos.BookRepo;
import com.eagledev.bookreaders.repos.CategoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final AuthorRepo authorRepo;
    private final BookRepo bookRepo;
    private final CategoryRepo categoryRepo;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // 1. Seed Categories first (Books depend on them)
        if (categoryRepo.count() == 0) {
            seedCategories();
        }
        else{
            log.info("[DataSeeder] Categories already exist.");
        }

        // 2. Seed Authors (Books depend on them)
        if (authorRepo.count() == 0) {
            seedAuthors();
        }
        else {
            log.info("[DataSeeder] Authors already exist.");
        }

        // 3. Seed Books (Links Authors + Categories)
        if (bookRepo.count() == 0) {
            seedBooks();
        }
        else {
            log.info("[DataSeeder] Books already exist.");
        }
    }

    private void seedCategories() {
        List<Category> categories = Arrays.asList(
                Category.builder().tag("Technology").description("Software, Computers, and AI").build(),
                Category.builder().tag("Science Fiction").description("Futuristic and space exploration").build(),
                Category.builder().tag("Fantasy").description("Magic, wizards, and epic quests").build(),
                Category.builder().tag("History").description("Past events and civilizations").build()
        );
        categoryRepo.saveAll(categories);
       log.info("[DataSeeder] Categories seeded.");
    }


    private void seedAuthors() {
        List<Author> authors = Arrays.asList(
                Author.builder()
                        .name("Robert C. Martin")
                        .bio("Uncle Bob, author of Clean Code.")
                        .nationality("American")
                        .dateOfBirth(LocalDate.of(1952, 12, 5))
                        .deleted(false)
                        .build(),
                Author.builder()
                        .name("J.R.R. Tolkien")
                        .bio("The father of modern fantasy literature.")
                        .nationality("British")
                        .dateOfBirth(LocalDate.of(1892, 1, 3))
                        .deleted(false)
                        .build(),
                Author.builder()
                        .name("Frank Herbert")
                        .bio("American science fiction author best known for the novel Dune.")
                        .nationality("American")
                        .dateOfBirth(LocalDate.of(1920, 10, 8))
                        .deleted(false)
                        .build()
        );
        authorRepo.saveAll(authors);
       log.info("[DataSeeder] Authors seeded.");
    }

    private void seedBooks() {

        Category techCat = categoryRepo.findByTag("Technology").orElseThrow();
        Category fantasyCat = categoryRepo.findByTag("Fantasy").orElseThrow();
        Category sciFiCat = categoryRepo.findByTag("Science Fiction").orElseThrow();
        

        Author uncleBob =authorRepo.findByNameContainingIgnoreCase("Robert").orElseThrow();
        Author tolkien = authorRepo.findByNameContainingIgnoreCase("Tolkien").orElseThrow();
        Author herbert = authorRepo.findByNameContainingIgnoreCase("Frank").orElseThrow();
        
        
        Book cleanCode = Book.builder()
                .title("Clean Code")
                .description("A Handbook of Agile Software Craftsmanship")
                .edition("1st")
                .numberOfPages(464)
                .price(BigDecimal.valueOf(45.00))
                .language("English")
                .publishDate(LocalDate.of(2008, 8, 1))
                .deleted(false)
                .authors(Collections.singletonList(uncleBob))
                .categories(Collections.singletonList(techCat))
                .build();

        DiscussionRoom room1 = new DiscussionRoom();
        room1.setBook(cleanCode);
        cleanCode.setDiscussionRoom(room1);

        // Book 2: The Hobbit
        Book hobbit = Book.builder()
                .title("The Hobbit")
                .description("In a hole in the ground there lived a hobbit.")
                .edition("Illustrated")
                .numberOfPages(310)
                .price(BigDecimal.valueOf(15.99))
                .language("English")
                .publishDate(LocalDate.of(1937, 9, 21))
                .deleted(false)
                .authors(Collections.singletonList(tolkien))
                .categories(Collections.singletonList(fantasyCat))
                .build();

        DiscussionRoom room2 = new DiscussionRoom();
        room2.setBook(hobbit);
        hobbit.setDiscussionRoom(room2);

        // Book 3: Dune
        Book dune = Book.builder()
                .title("Dune")
                .description("Set on the desert planet Arrakis...")
                .edition("Deluxe")
                .numberOfPages(412)
                .price(BigDecimal.valueOf(20.50))
                .language("English")
                .publishDate(LocalDate.of(1965, 8, 1))
                .deleted(false)
                .authors(Collections.singletonList(herbert))
                .categories(Collections.singletonList(sciFiCat))
                .build();

        DiscussionRoom room3 = new DiscussionRoom();
        room3.setBook(dune);
        dune.setDiscussionRoom(room3);

        bookRepo.saveAll(Arrays.asList(cleanCode, hobbit, dune));
       log.info("[DataSeeder] Books seeded.");
    }
}