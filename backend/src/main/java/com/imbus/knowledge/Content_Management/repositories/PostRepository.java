package com.imbus.knowledge.Content_Management.repositories;

import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.User_Management.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthor(User user);
    Page<Post> findAll(Pageable pageable); // For pagination
}
