package com.back.domain.post.comment.entity;

import com.back.domain.member.entity.Member;
import com.back.domain.post.post.entity.Post;
import com.back.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
// 댓글은 어떤 게시글에 속하고, 누가 작성했는지를 함께 가지는 하위 엔티티
public class Comment extends BaseEntity {
    private String content;

    @ManyToOne
    private Post post;

    @ManyToOne
    private Member author;

    public Comment(Member author, String content, Post post) {
        this.author = author;
        this.content = content;
        this.post = post;
    }

    public void update(String content) {
        this.content = content;
    }
}
