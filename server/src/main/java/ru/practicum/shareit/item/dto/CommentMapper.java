package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> comments) {
        List<CommentDto> commentDtos = new ArrayList<>();
        if (comments == null || comments.isEmpty()) {
            return commentDtos;
        }
        for (Comment comment : comments) {
            commentDtos.add(CommentMapper.toCommentDto(comment));
        }
        return commentDtos;
    }

    public static Comment dtoToComment(CommentDto commentDto, User user, Item item) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(
                commentDto.getCreated() == null ? LocalDateTime.now() : commentDto.getCreated()
        );
        return comment;
    }
}
