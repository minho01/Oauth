package com.back.domain.post.comment.controller;

import com.back.domain.post.comment.dto.CommentDto;
import com.back.domain.post.comment.entity.Comment;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/comments")
public class ApiV1CommentController {

    private final PostService postService;

    @GetMapping
    @ResponseBody
    public List<CommentDto> list(
            @PathVariable int postId
    ) {
        Post post = postService.findById(postId).get();
        List<Comment> comments = post.getComments();

        List<CommentDto> commentDtoList = comments.stream()
                .map(CommentDto::new)
                .toList();

        return commentDtoList;
    }

    @GetMapping("/{commentId}")
    @ResponseBody
    public CommentDto detail(@PathVariable int postId, @PathVariable int commentId) {
        Post post = postService.findById(postId).get();
        Comment comment = post.findCommentById(commentId).get();

        return new CommentDto(comment);
    }
}
