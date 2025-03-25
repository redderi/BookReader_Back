package com.redderi.bookreaderback.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public class TagDTO {

    private Long id;

    @NotBlank(message = "Название тега обязательно")
    private String name;

    private Set<String> bookTitles;


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

    public Set<String> getBookTitles() {
        return bookTitles;
    }

    public void setBookTitles(Set<String> bookTitles) {
        this.bookTitles = bookTitles;
    }
}