package com.redderi.bookreaderback.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class AuthorDTO {

    private Long id;

    @NotBlank(message = "Имя автора обязательно")
    private String name;

    private List<String> bookTitles; 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getBookTitles() {
        return bookTitles;
    }

    public void setBookTitles(List<String> bookTitles) {
        this.bookTitles = bookTitles;
    }
}