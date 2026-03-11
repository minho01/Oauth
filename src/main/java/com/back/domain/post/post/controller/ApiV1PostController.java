package com.back.domain.post.post.controller;

import com.back.domain.post.post.dto.PostDto;
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
@RequestMapping("/api/v1/posts")
public class ApiV1PostController {

    private final PostService postService;

    @GetMapping
    @ResponseBody
    public List<PostDto> list() {
        List<Post> result = postService.findAll();

        List<PostDto> postDtoList = result.stream()
                .map(PostDto::new)
                .toList();

        return postDtoList;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public PostDto detail(@PathVariable int id) {
        Post post = postService.findById(id).get();
        return new PostDto(post);
    }
}
