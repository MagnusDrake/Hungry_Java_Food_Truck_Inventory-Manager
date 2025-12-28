package com.example.demo.repositories;

import com.example.demo.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

    // This gives you methods like .save(), .findAll(), .delete() for free!
    public interface UserRepository extends JpaRepository<UserEntity, Long> {
    }
