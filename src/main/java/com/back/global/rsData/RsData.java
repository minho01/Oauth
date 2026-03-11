package com.back.global.rsData;

import com.back.domain.post.comment.dto.CommentDto;

public record RsData(
        String msg,
        String resultCode,
        CommentDto data
) {
}
