package com.redderi.bookreaderback.controller;

import com.redderi.bookreaderback.dto.ApiResponse;
import com.redderi.bookreaderback.dto.BookDTO;
import com.redderi.bookreaderback.dto.BookIdRequest;
import com.redderi.bookreaderback.dto.LoginRequest;
import com.redderi.bookreaderback.dto.PasswordChangeRequest;
import com.redderi.bookreaderback.dto.UsernameChangeRequest;
import com.redderi.bookreaderback.dto.UserDTO; // Импортируйте ваш UserDTO
import com.redderi.bookreaderback.model.Quote;
import com.redderi.bookreaderback.model.User;
import com.redderi.bookreaderback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/addNewUser")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody User user) {
        String result = userService.registerUser(user);
        ApiResponse response = new ApiResponse();

        if (result.equals("The user has been successfully registered")) {
            URI location = URI.create("/api/users/" + user.getUsername());
            response.setStatus("Success");
            response.setMessage(result);
            return ResponseEntity.created(location).body(response);
        }

        response.setStatus("Error");
        response.setMessage(result);
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO userDTO = userService.getUserByUsername(username);
        if (userDTO == null) {
            return ResponseEntity.status(404).body(null); 
        }
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable String username) {
        String result = userService.deleteUser(username);
        ApiResponse response;
        if (result.equals("The user was not found")) {
            response = new ApiResponse("Error", result);
            return ResponseEntity.status(404).body(response);
        }
        response = new ApiResponse("Success", result);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/changePassword/{username}")
    public ResponseEntity<ApiResponse> changePassword(@PathVariable String username, 
            @RequestBody PasswordChangeRequest request) {
        String result = userService.changePassword(username, request.getOldPassword(), request.getNewPassword());
        ApiResponse response;
        if (result.equals("The user was not found")) {
            response = new ApiResponse("Error", result);
            return ResponseEntity.status(404).body(response);
        }
        response = new ApiResponse("Success", result);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/changeUsername/{username}")
    public ResponseEntity<ApiResponse> changeUsername(@PathVariable String username, 
            @RequestBody UsernameChangeRequest request) {
        String result = userService.changeUsername(username, request.getNewUsername());
        ApiResponse response;
        if (result.equals("The user was not found")) {
            response = new ApiResponse("Error", result);
            return ResponseEntity.status(404).body(response);
        }
        response = new ApiResponse("Success", result);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest) {
        String result = userService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
        
        if (result.equals("Success: the user is logged in")) {
            return ResponseEntity.ok(new ApiResponse("Success", result));
        } else {
            return ResponseEntity.status(401).body(new ApiResponse("Error", result)); 
        }
    }

    @PostMapping("/{username}/books")
    public ResponseEntity<ApiResponse> addBookToUser(@PathVariable String username, @Valid @RequestBody BookIdRequest bookIdRequest) {
        String result = userService.addBookToUser(username, bookIdRequest.getBookId());
        ApiResponse response = new ApiResponse();
        if (result.equals("Book success added to user")) {
            response.setStatus("Success");
            response.setMessage(result);
            return ResponseEntity.ok(response);
        }
        response.setStatus("Error");
        response.setMessage(result);
        return ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/{username}/books/{bookId}")
    public ResponseEntity<ApiResponse> removeBookFromUser(@PathVariable String username, @PathVariable Long bookId) {
        String result = userService.removeBookFromUser(username, bookId);
        ApiResponse response = new ApiResponse();
        if (result.equals("The book was successfully deleted from the user")) {
            response.setStatus("Success");
            response.setMessage(result);
            return ResponseEntity.ok(response);
        }
        response.setStatus("Error");
        response.setMessage(result);
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/{username}/books")
    public ResponseEntity<Set<BookDTO>> getUserBooks(@PathVariable String username) {
        Set<BookDTO> books = userService.getUserBooks(username);
        if (books == null) {
            return ResponseEntity.status(404).body(null); 
        }
        return ResponseEntity.ok(books);
    }
    
    @PostMapping("/{username}/quotes")
    public ResponseEntity<ApiResponse> addQuoteToUser(@PathVariable String username,
                                                       @RequestBody Quote quote) {
        try {
            Long userId =  userService.getUserIdByUsername(username);
            String result = userService.addQuoteToUser(userId, quote);
            ApiResponse response = new ApiResponse();
            if (result.equals("Quote successfully added")) {
                response.setStatus("Success");
                response.setMessage(result);
                return ResponseEntity.ok(response);
            }
            response.setStatus("Error");
            response.setMessage(result);
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            ApiResponse response = new ApiResponse("Error", "User not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    @DeleteMapping("/{username}/quotes/{index}")
    public ResponseEntity<ApiResponse> removeQuoteFromUser(@PathVariable String username,
                                                           @PathVariable int index) {
        try {
            Long userId =  userService.getUserIdByUsername(username);
            String result = userService.removeQuoteFromUser(userId, index);
            ApiResponse response = new ApiResponse();
            if (result.equals("Quote successfully removed")) {
                response.setStatus("Success");
                response.setMessage(result);
                return ResponseEntity.ok(response);
            }
            response.setStatus("Error");
            response.setMessage(result);
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            ApiResponse response = new ApiResponse("Error", "User not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    @PutMapping("/{username}/quotes/{index}")
    public ResponseEntity<ApiResponse> updateQuoteOfUser(@PathVariable String username,
                                                         @PathVariable int index,
                                                         @RequestBody Quote newQuote) {
        try {
            Long userId =  userService.getUserIdByUsername(username);
            String result = userService.updateQuoteOfUser(userId, index, newQuote);
            ApiResponse response = new ApiResponse();
            if (result.equals("Quote successfully updated")) {
                response.setStatus("Success");
                response.setMessage(result);
                return ResponseEntity.ok(response);
            }
            response.setStatus("Error");
            response.setMessage(result);
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            ApiResponse response = new ApiResponse("Error", "User not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    @GetMapping("/{username}/quotes")
    public ResponseEntity<List<Quote>> getUserQuotes(@PathVariable String username) {
        try {
            Long userId = userService.getUserIdByUsername(username);
            List<Quote> quotes = userService.getUserQuotes(userId);
            if (quotes == null) {
                return ResponseEntity.status(404).body(null);
            }
            return ResponseEntity.ok(quotes);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }
}