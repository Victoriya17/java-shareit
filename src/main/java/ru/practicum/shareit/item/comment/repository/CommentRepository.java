package ru.practicum.shareit.item.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemId(Long itemId);

    @Query("SELECT c " +
            "FROM Comment as c " +
            "WHERE c.item.id in (?1) " +
            "ORDER BY c.created DESC")
    List<Comment> findByItemIn(List<Long> ids);

}
