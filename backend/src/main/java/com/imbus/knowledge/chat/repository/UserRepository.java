package com.imbus.knowledge.chat.repository;

import com.imbus.knowledge.User_Management.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.id IN :ids")
    Set<User> findAllByIdIn(@Param("ids") Set<Long> ids);

    boolean existsByUsername(String username);
}