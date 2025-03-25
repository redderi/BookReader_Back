package com.redderi.bookreaderback.service;

import com.redderi.bookreaderback.dto.BookDTO; 
import com.redderi.bookreaderback.dto.UserDTO; 
import com.redderi.bookreaderback.model.User;
import com.redderi.bookreaderback.model.Book;
import com.redderi.bookreaderback.model.Quote;
import com.redderi.bookreaderback.model.Tag;
import com.redderi.bookreaderback.repository.UserRepository;
import com.redderi.bookreaderback.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository; 
    
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return "Username is taken";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "The user has been successfully registered";
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return user != null ? convertToDTO(user) : null;
    }

    public String deleteUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "The user was not found";
        }
        userRepository.delete(user);
        return "The user has been successfully deleted";
    }

    public String changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "The user was not found";
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return "The old password is incorrect";
        }
        user.setPassword(passwordEncoder.encode(newPassword)); 
        userRepository.save(user);
        return "The password has been successfully changed";
    }

    public String changeUsername(String username, String newUsername) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "The user was not found";
        }
        if (userRepository.existsByUsername(newUsername)) {
            return "The username is already taken";
        }
        user.setUsername(newUsername);
        userRepository.save(user);
        return "The username has been successfully changed";
    }
    
    public String authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "The user was not found";
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "Invalid password";
        }
        return "Success: the user is logged in";
    }

    public String addBookToUser(String username, Long bookId) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "User not founded";
        }

        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            return "Book not founded";
        }

        user.getBooks().add(book.getId());
        userRepository.save(user);

        return "Book success added to user";
    }
    
    public String removeBookFromUser(String username, Long bookId) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "The user was not found";
        }

        user.getBooks().remove(bookId);
        userRepository.save(user);
        return "The book was successfully deleted from the user";
    }

    public Set<BookDTO> getUserBooks(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null; 
        }

        return user.getBooks().stream()
            .map(bookId -> {
                Book book = bookRepository.findById(bookId).orElse(null);
                if (book != null) {
                    BookDTO bookDTO = new BookDTO();
                    bookDTO.setId(book.getId());
                    bookDTO.setTitle(book.getTitle());
                    
                    if (book.getAuthor() != null) {
                        bookDTO.setAuthorName(book.getAuthor().getName());
                    }
                    
                    bookDTO.setYear(book.getYear());
                    bookDTO.setRating(book.getRating());
                    bookDTO.setFilePath(book.getFilePath());
                    bookDTO.setCoverImagePath(book.getCoverImagePath());
                    
                    Set<String> tagNames = book.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toSet());
                    bookDTO.setTagNames(tagNames);
                    
                    return bookDTO;
                }
                return null;
            })
            .collect(Collectors.toSet());
    }
    
    public String addQuoteToUser(Long userId, Quote quote) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return "User not found";
        }
        User user = userOpt.get();
        user.addQuote(quote);
        userRepository.save(user);
        return "Quote successfully added";
    }

    public String removeQuoteFromUser(Long userId, int index) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return "User not found";
        }
        User user = userOpt.get();
        user.removeQuote(index);
        userRepository.save(user);
        return "Quote successfully removed";
    }

    public String updateQuoteOfUser(Long userId, int index, Quote newQuote) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return "User not found";
        }
        User user = userOpt.get();
        user.updateQuote(index, newQuote);
        userRepository.save(user);
        return "Quote successfully updated";
    }

    public List<Quote> getUserQuotes(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return null;
        }
        return userOpt.get().getQuotes();
    }
    
    public Long getUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user.getId();
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setBooks(user.getBooks());
        return userDTO;
    }
}