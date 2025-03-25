package com.redderi.bookreaderback.dto;

import jakarta.validation.constraints.NotNull;

public class BookIdRequest {
    @NotNull(message = "Book ID is required")
    private Long bookId;

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}