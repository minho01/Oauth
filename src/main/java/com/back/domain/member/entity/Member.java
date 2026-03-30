package com.back.domain.member.entity;

import com.back.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
// 회원가입, 로그인, 글 작성자의 주체가 되는 회원 엔티티
public class Member extends BaseEntity {
    @Column(unique = true)
    private String username;
    private String password;
    private String nickname;
    @Column(unique = true)
    private String apiKey;

    public Member(String username, String password, String nickname, String apiKey) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.apiKey = apiKey;
    }

    public String getName() {
        return nickname;
    }
}
