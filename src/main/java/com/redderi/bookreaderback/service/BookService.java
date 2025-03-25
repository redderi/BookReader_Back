package com.redderi.bookreaderback.service;

import com.redderi.bookreaderback.model.Book;
import com.redderi.bookreaderback.model.Author;
import com.redderi.bookreaderback.model.Tag;
import com.redderi.bookreaderback.repository.BookRepository;
import com.redderi.bookreaderback.repository.AuthorRepository;
import com.redderi.bookreaderback.repository.TagRepository; 

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final TagRepository tagRepository; 
    private final ObjectMapper objectMapper;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, TagRepository tagRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.tagRepository = tagRepository; 
        this.objectMapper = new ObjectMapper();
    }

    public Book createBook(MultipartFile file, MultipartFile coverImage, String bookJson) {
        Book book = deserializeBook(bookJson);
        Author author = book.getAuthor();
        if (author != null) {
            Optional<Author> existingAuthor = authorRepository.findById(author.getId());
            if (existingAuthor.isPresent()) {
                book.setAuthor(existingAuthor.get()); 
            } else {
                authorRepository.save(author); 
                book.setAuthor(author);
            }
        }

        Set<Tag> tags = book.getTags() != null ? book.getTags() : new HashSet<>();
        Set<Tag> savedTags = new HashSet<>();
        
        for (Tag tag : tags) {
            Optional<Tag> existingTag = tagRepository.findById(tag.getId());
            if (existingTag.isPresent()) {
                savedTags.add(existingTag.get()); 
            } else {
                savedTags.add(tagRepository.save(tag)); 
            }
        }
        
        book.setTags(savedTags);

        String filePath = saveFile(file);
        String coverImagePath = saveCoverImage(coverImage);
        
        book.setFilePath(filePath);
        book.setCoverImagePath(coverImagePath);

        return bookRepository.save(book);
    }
    
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book updateBook(Long id, MultipartFile file, MultipartFile coverImage, String bookJson) {
        Book updatedBook = deserializeBook(bookJson);
        updatedBook.setId(id);
        
        Author author = updatedBook.getAuthor();
        if (author != null) {
            Optional<Author> existingAuthor = authorRepository.findById(author.getId());
            if (existingAuthor.isPresent()) {
                updatedBook.setAuthor(existingAuthor.get());
            } else {
                authorRepository.save(author);
                updatedBook.setAuthor(author);
            }
        }

        Set<Tag> tags = updatedBook.getTags();
        if (tags != null) {
            Set<Tag> savedTags = new HashSet<>();
            for (Tag tag : tags) {
                Optional<Tag> existingTag = tagRepository.findById(tag.getId());
                if (existingTag.isPresent()) {
                    savedTags.add(existingTag.get());
                } else {
                    savedTags.add(tagRepository.save(tag)); 
                }
            }
            updatedBook.setTags(savedTags);
        }

        if (file != null && !file.isEmpty()) {
            String filePath = saveFile(file);
            updatedBook.setFilePath(filePath);
        }
        if (coverImage != null && !coverImage.isEmpty()) {
            String coverImagePath = saveCoverImage(coverImage);
            updatedBook.setCoverImagePath(coverImagePath);
        }
        return bookRepository.save(updatedBook);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + id));

        for (Tag tag : book.getTags()) {
            tag.getBooks().remove(book); 
        }
        book.getTags().clear();

        bookRepository.delete(book);
    }

    public String saveFile(MultipartFile file) {
        try {
            String uploadDir = "C:\\Users\\Lenovo\\Desktop\\пмс\\bdBooks"; 
            File directory = new File(uploadDir);
            
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = uploadDir + File.separator + file.getOriginalFilename();
            File destinationFile = new File(filePath);
            file.transferTo(destinationFile);
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException("Error to save file: " + e.getMessage(), e);
        }
    }

    public String saveCoverImage(MultipartFile coverImage) {
        try {
            String uploadDir = "C:\\Users\\Lenovo\\Desktop\\пмс\\bdBooks\\covers";
            File directory = new File(uploadDir);
            
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String coverImagePath = uploadDir + File.separator + coverImage.getOriginalFilename();
            File destinationFile = new File(coverImagePath);
            coverImage.transferTo(destinationFile);
            return coverImagePath;
        } catch (IOException e) {
            throw new RuntimeException("Error to save book cover: " + e.getMessage(), e);
        }
    }

    private Book deserializeBook(String bookJson) {
        try {
            return objectMapper.readValue(bookJson, Book.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error to deserialize book", e);
        }
    }

    public String readTxtFile(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error to read txt file", e);
        }
    }

    public String readPdfFile(MultipartFile file) {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        } catch (IOException e) {
            throw new RuntimeException("Error to read pdf-file", e);
        }
    }
    
    public List<Book> getBooksByTag(Long tagId) {
        return bookRepository.findBooksByTagsId(tagId);
    }
   
}