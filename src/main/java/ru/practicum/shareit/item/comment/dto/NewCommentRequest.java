package ru.practicum.shareit.item.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class NewCommentRequest {
    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;

    @Positive(message = "Id вещи, о которой пишут комментарий, должно быть больше нуля")
    private Long itemId;

    @Positive(message = "Id пользователя, который оставляет комментарий, должно быть больше нуля")
    private Long authorId;
}
