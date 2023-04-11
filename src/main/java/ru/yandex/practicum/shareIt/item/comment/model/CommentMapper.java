package ru.yandex.practicum.shareIt.item.comment.model;

import ru.yandex.practicum.shareIt.item.comment.model.Comment;
import ru.yandex.practicum.shareIt.item.comment.model.CommentDto;
import ru.yandex.practicum.shareIt.item.comment.model.CommentRequestDto;

public class CommentMapper {

    public static CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getUser().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public static Comment mapToCommentFromCommentRequestDto(CommentRequestDto commentRequestDto) {
        Comment comment = new Comment();
        comment.setText(commentRequestDto.getText());
        return comment;
    }
}
