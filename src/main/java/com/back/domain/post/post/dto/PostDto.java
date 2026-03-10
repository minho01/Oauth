package com.back.domain.post.post.dto;

import java.time.LocalDateTime;

public record PostDto(
        int id,
        String title,
        String content,
        LocalDateTime createdDate,
        LocalDateTime modifyDate
) {
}
