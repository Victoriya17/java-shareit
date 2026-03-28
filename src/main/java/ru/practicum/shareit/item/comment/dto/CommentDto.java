package ru.practicum.shareit.item.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String text;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long itemId;
    private String authorName;
    private LocalDateTime created;
}
