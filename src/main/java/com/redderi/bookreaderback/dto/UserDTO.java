package com.redderi.bookreaderback.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public class UserDTO {

    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    private Set<Long> books;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Long> getBooks() {
        return books;
    }

    public void setBooks(Set<Long> books) {
        this.books = books;
    }
}