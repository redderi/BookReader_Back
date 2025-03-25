package com.redderi.bookreaderback.repository;

import com.redderi.bookreaderback.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	
    @Query("SELECT b FROM Book b JOIN b.tags t WHERE t.id = :tagId")
    List<Book> findBooksByTagsId(@Param("tagId") Long tagId);
}