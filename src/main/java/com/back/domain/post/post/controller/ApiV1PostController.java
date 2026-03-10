package com.back.domain.post.post.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ApiV1PostController {

    private final PostService postService;

    @GetMapping("/api/v1/posts")
    @ResponseBody
    public List<Post> list() {
        return postService.findAll();
    }
}
