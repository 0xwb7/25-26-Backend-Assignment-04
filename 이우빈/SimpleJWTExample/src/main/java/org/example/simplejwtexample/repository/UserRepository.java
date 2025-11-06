package org.example.simplejwtexample.repository;

import org.example.simplejwtexample.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
