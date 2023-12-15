package com.vikram.blogapp.repository;

import com.vikram.blogapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
