package com.imbus.knowledge.Content_Management.repositories;

import com.imbus.knowledge.Content_Management.entities.Favorite;
import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.User_Management.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByUserAndPost(User user, Post post);
    Optional<Favorite> findByUserAndPost(@Param("user") User user, @Param("post") Post post);
    @Query("SELECT f FROM Favorite f WHERE f.user.id = ?1")
    List<Favorite> findByUserId(Long userId);
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.user.id = ?1")
    int countByUserId(Long userId);
}
