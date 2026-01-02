package com.eagledev.bookreaders.services.author;

import com.eagledev.bookreaders.entities.Author;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import com.eagledev.bookreaders.repos.AuthorRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService{

    private final AuthorRepo authorRepo;


    @Override
    public Author getAuthorById(UUID authorId) {
        return authorRepo.findByUuid(authorId).orElseThrow(
                () -> new ResourceNotFoundException("Author","id",authorId)
        );
    }
}
