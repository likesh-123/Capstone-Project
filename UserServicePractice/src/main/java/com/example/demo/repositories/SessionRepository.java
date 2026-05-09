package com.example.demo.repositories;

import com.example.demo.models.Session;
import com.example.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
    User save(User user);

//    User UpdateSessionByEmail;
}
