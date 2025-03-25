package com.redderi.bookreaderback.repository;

import com.redderi.bookreaderback.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

	Tag findByName(String tagName);
}