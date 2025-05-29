package com.imbus.knowledge.Content_Management.repositories;

import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.Content_Management.entities.Share;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShareRepository extends JpaRepository<Share, Long> {
    @EntityGraph(attributePaths = {"user", "post"})
    List<Share> findByPost(Post post);

    long countByPost(Post post);
}
