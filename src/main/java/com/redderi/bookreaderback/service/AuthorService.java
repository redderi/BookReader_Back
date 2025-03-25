package com.redderi.bookreaderback.service;

import com.redderi.bookreaderback.model.Author;
import com.redderi.bookreaderback.model.Book;
import com.redderi.bookreaderback.repository.AuthorRepository;
import com.redderi.bookreaderback.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }
    public Author createAuthor(Author author) {
        return authorRepository.save(author);
    }

    public List<Author> getAllAuthors() {		
        return authorRepository.findAll();
    }

    public Optional<Author> getAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    public Author updateAuthor(Long id, Author authorDetails) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author not founded with ID: " + id));

        author.setName(authorDetails.getName());
        return authorRepository.save(author);
    }

    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new IllegalArgumentException("Author not founded with ID: " + id);
        }
        authorRepository.deleteById(id);
    }

    public Author addBookToAuthor(Long authorId, Long bookId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Author not founded with ID: " + authorId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not founded with ID: " + bookId));

        book.setAuthor(author); 
        bookRepository.save(book); 

        author.getBooks().add(book); 
        return authorRepository.save(author); 
    }


    public Book updateBookForAuthor(Long authorId, Long bookId, Book bookDetails) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Author not founded with ID: " + authorId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not founded with ID: " + bookId));

        book.setTitle(bookDetails.getTitle());
        book.setYear(bookDetails.getYear());
        book.setRating(bookDetails.getRating());
        book.setFilePath(bookDetails.getFilePath());
        book.setCoverImagePath(bookDetails.getCoverImagePath());
        book.setTags(bookDetails.getTags()); 

        return bookRepository.save(book); 
    }

    public void removeBookFromAuthor(Long authorId, Long bookId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Author not founded with ID: " + authorId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not founded with ID: " + bookId));

        author.getBooks().remove(book);
        authorRepository.save(author); 
        //bookRepository.delete(book);
    }
}