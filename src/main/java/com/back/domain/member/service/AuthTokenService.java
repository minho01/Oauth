package com.back.domain.member.service;

import com.back.domain.member.entity.Member;
import com.back.standard.ut.Ut;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
class AuthTokenService {

    private final String secretKey = "abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz1234567890";
    private final long expireTime = 1000L * 60 * 60 * 24 * 365; // 1년

    String genAccessToken(Member member) {
        return Ut.jwt.toString(
                secretKey,
                expireTime,
                Map.of(
                        "id", member.getId(),
                        "name", member.getName()
                )
        );
    }
    Map<String, Object> payloadOrNull(String jwt) {
        return Ut.jwt.payloadOrNull(jwt, secretKey);
    }
}
