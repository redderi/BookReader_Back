package com.redderi.bookreaderback.repository;

import com.redderi.bookreaderback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByUsername(String username);
    User findByUsername(String username);
}