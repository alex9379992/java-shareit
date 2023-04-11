package ru.yandex.practicum.shareIt.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.shareIt.item.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemId(Long itemId);
}
