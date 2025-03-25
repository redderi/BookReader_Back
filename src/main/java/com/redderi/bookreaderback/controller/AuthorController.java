package com.redderi.bookreaderback.controller;

import com.redderi.bookreaderback.dto.AuthorDTO;
import com.redderi.bookreaderback.model.Author;
import com.redderi.bookreaderback.model.Book;
import com.redderi.bookreaderback.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<AuthorDTO> createAuthor(@RequestBody Author author) {
        Author createdAuthor = authorService.createAuthor(author);
        AuthorDTO authorDTO = convertToDTO(createdAuthor);
        return ResponseEntity.ok(authorDTO);
    }

    @GetMapping
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        List<Author> authors = authorService.getAllAuthors();
        List<AuthorDTO> authorDTOs = authors.stream()
                                             .map(this::convertToDTO)
                                             .collect(Collectors.toList());
        return ResponseEntity.ok(authorDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {
        return authorService.getAuthorById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDTO> updateAuthor(
            @PathVariable Long id,
            @RequestBody Author authorDetails) {
        Author updatedAuthor = authorService.updateAuthor(id, authorDetails);
        AuthorDTO authorDTO = convertToDTO(updatedAuthor);
        return ResponseEntity.ok(authorDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.ok("The author has been successfully deleted");
    }

    @PostMapping("/{authorId}/books/{bookId}")
    public ResponseEntity<AuthorDTO> addBookToAuthor(
            @PathVariable Long authorId,
            @PathVariable Long bookId) {
        Author updatedAuthor = authorService.addBookToAuthor(authorId, bookId);
        AuthorDTO authorDTO = convertToDTO(updatedAuthor);
        return ResponseEntity.ok(authorDTO);
    }

    @PutMapping("/{authorId}/books/{bookId}")
    public ResponseEntity<Book> updateBookForAuthor(
            @PathVariable Long authorId,
            @PathVariable Long bookId,
            @RequestBody Book bookDetails) {
        Book updatedBook = authorService.updateBookForAuthor(authorId, bookId, bookDetails);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{authorId}/books/{bookId}")
    public ResponseEntity<String> removeBookFromAuthor(
            @PathVariable Long authorId,
            @PathVariable Long bookId) {
        authorService.removeBookFromAuthor(authorId, bookId);
        return ResponseEntity.ok("The book was successfully deleted from the author");
    }

    private AuthorDTO convertToDTO(Author author) {
        AuthorDTO dto = new AuthorDTO();
        dto.setId(author.getId());
        dto.setName(author.getName());
        List<String> bookTitles = author.getBooks().stream()
                                         .map(Book::getTitle)
                                         .collect(Collectors.toList());
        dto.setBookTitles(bookTitles);

        return dto;
    }
}