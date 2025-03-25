package com.redderi.bookreaderback.controller;

import com.redderi.bookreaderback.dto.BookDTO;
import com.redderi.bookreaderback.model.Book;
import com.redderi.bookreaderback.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BookDTO> createBook(
            @RequestParam("file") MultipartFile file, 
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam("book") String bookJson) {
        Book createdBook = bookService.createBook(file, coverImage, bookJson);
        BookDTO bookDTO = convertToDTO(createdBook);
        return ResponseEntity.ok(bookDTO);
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        List<BookDTO> bookDTOs = books.stream()
                                       .map(this::convertToDTO)
                                       .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable Long id) {
        Optional<Book> bookOptional = bookService.getBookById(id);
        if (!bookOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Book book = bookOptional.get();
        Path filePath = Paths.get(book.getFilePath());
        FileSystemResource resource = new FileSystemResource(filePath.toFile());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_PDF) 
                .body(resource);
    }

    @GetMapping("/cover/{id}")
    public ResponseEntity<FileSystemResource> getCoverImage(@PathVariable Long id) {
        Optional<Book> bookOptional = bookService.getBookById(id);
        if (!bookOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Book book = bookOptional.get();
        Path coverImagePath = Paths.get(book.getCoverImagePath());
        FileSystemResource resource = new FileSystemResource(coverImagePath.toFile());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.IMAGE_JPEG) 
                .body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam("book") String bookJson) {
        Book updatedBook = bookService.updateBook(id, file, coverImage, bookJson);
        BookDTO bookDTO = convertToDTO(updatedBook);
        return ResponseEntity.ok(bookDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("The book was successfully deleted");
    }

    @GetMapping("/tag/{tagId}")
    public ResponseEntity<List<BookDTO>> getBooksByTag(@PathVariable Long tagId) {
        List<Book> books = bookService.getBooksByTag(tagId);
        List<BookDTO> bookDTOs = books.stream()
                                       .map(this::convertToDTO)
                                       .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }

    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthorName(book.getAuthor() != null ? book.getAuthor().getName() : null);
        dto.setYear(book.getYear());
        dto.setRating(book.getRating());
        dto.setFilePath(book.getFilePath());
        dto.setCoverImagePath(book.getCoverImagePath());
        Set<String> tagNames = book.getTags().stream()
                                   .map(tag -> tag.getName())
                                   .collect(Collectors.toSet());
        dto.setTagNames(tagNames);

        return dto;
    }
}