package com.redderi.bookreaderback.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "book_reader_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @ElementCollection
    @CollectionTable(name = "user_books", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "book_id")
    private Set<Long> books = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_quotes", joinColumns = @JoinColumn(name = "user_id"))
    private List<Quote> quotes;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Long> getBooks() {
        return books;
    }

    public void setBooks(Set<Long> books) {
        this.books = books;
    }

    public List<Quote> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<Quote> quotes) {
        this.quotes = quotes;
    }

    public void addQuote(Quote quote) {
        this.quotes.add(quote);
    }

    public void removeQuote(int index) {
        if (index >= 0 && index < quotes.size()) {
            this.quotes.remove(index);
        }
    }

    public void updateQuote(int index, Quote newQuote) {
        if (index >= 0 && index < quotes.size()) {
            this.quotes.set(index, newQuote);
        }
    }
}
