package com.redderi.bookreaderback.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public class BookDTO {

    private Long id;

    @NotBlank(message = "Название книги не должно быть пустым")
    private String title;

    @NotBlank(message = "Имя автора не должно быть пустым")
    private String authorName;

    @Min(value = 1000, message = "Год выпуска должен быть не ранее 1000 года")
    private int year;

    @Min(value = 0, message = "Оценка должна быть не менее 0")
    @Max(value = 5, message = "Оценка должна быть не более 5")
    private double rating;

    private String filePath;

    private String coverImagePath;

    private Set<String> tagNames; 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }

    public void setCoverImagePath(String coverImagePath) {
        this.coverImagePath = coverImagePath;
    }

    public Set<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(Set<String> tagNames) {
        this.tagNames = tagNames;
    }
}