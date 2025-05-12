package com.imbus.knowledge.User_Management.repositories;

import com.imbus.knowledge.User_Management.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);

    boolean existsByName(String name);

    boolean existsByEmail(String email);

    @Transactional
    @Modifying
    @Query("update User u set u.password = ?2 where u.email = ?1")
    void updatePassword(String email, String password);

    @Modifying
    @Transactional
    @Query("UPDATE User u " +
            "SET u.name = :name, " +
            "u.bio = :bio, " +
            "u.location = :location, " +
            "u.phoneNumber = :phoneNumber, " +
            "u.github = :github, " +
            "u.linkedin = :linkedin, " +
            "u.avatarUrl = :avatarUrl " +
            "WHERE u.id = :userId")
    void updateProfile(
            @Param("userId") Long userId,
            @Param("name") String name,
            @Param("bio") String bio,
            @Param("location") String location,
            @Param("phoneNumber") String phoneNumber,
            @Param("github") String github,
            @Param("linkedin") String linkedin,
            @Param("avatarUrl") String avatarUrl
    );

    @Modifying
    @Query("UPDATE User u SET u.lastLogin = ?2 WHERE u.id = ?1")
    void updateLastLogin(Long userId, LocalDateTime lastLogin);
}
