package com.back.domain.post.comment.controller;

import com.back.domain.post.comment.dto.CommentDto;
import com.back.domain.post.comment.entity.Comment;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.repository.PostRepository;
import com.back.domain.post.post.service.PostService;
import com.back.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/comments")
public class ApiV1CommentController {

    private final PostService postService;
    private final PostRepository postRepository;

    @GetMapping
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
    public CommentDto detail(@PathVariable int postId, @PathVariable int commentId) {
        Post post = postService.findById(postId).get();
        Comment comment = post.findCommentById(commentId).get();

        return new CommentDto(comment);
    }


    record CommentWriteReqBody(
            @NotBlank(message = "02-content-내용은 필수입니다.")
            @Size(min = 2, max = 100, message = "04-content-내용은 2자 이상 100자 이하로 입력해주세요.")
            String content
    ) {
    }

    record CommentWriteResBody(
            CommentDto commentDto
    ) {
    }

    @PostMapping
    @Transactional
    public RsData<CommentWriteResBody> write(
            @PathVariable int postId,
            @RequestBody @Valid CommentWriteReqBody reqBody
    ) {

        Post post = postService.findById(postId).get();
        Comment comment = post.addComment(reqBody.content);

        return new RsData<>(
                "%d번 댓글이 성공적으로 작성되었습니다.".formatted(comment.getId()),
                "201-1",
                new CommentWriteResBody(
                        new CommentDto(comment)
                )
        );
    }

    @DeleteMapping("/{commentId}")
    @Transactional
    public RsData<CommentDto> delete(
            @PathVariable int postId,
            @PathVariable int commentId
    ) {
        Post post = postService.findById(postId).get();
        Comment comment = post.findCommentById(commentId).get();
        post.deleteComment(commentId);

        return new RsData<>(
                "%d번 댓글이 삭제되었습니다.".formatted(commentId),
                "204-1",
                new CommentDto(comment)
        );
    }
}
