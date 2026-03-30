package com.back.global.rq;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.global.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope // 각 HTTP 요청마다 새로운 Rq 객체가 생성되고, 요청이 끝나면 해당 객체는 소멸됩니다.
@RequiredArgsConstructor
public class Rq {

    private final HttpServletRequest request;
    private final MemberService memberService;

    public Member getActor() {

        String authorizationHeader = request.getHeader("Authorization");

        if(authorizationHeader == null) {
            throw new ServiceException("401-1", "인증 정보가 헤더에 존재하지 않습니다.");
        }

        if(!authorizationHeader.startsWith("Bearer ")) {
            throw new ServiceException("401-2", "잘못된 형식의 인증데이터입니다.");
        }

        String apiKey = authorizationHeader.replace("Bearer ", "");

        return memberService.findByApiKey(apiKey).orElseThrow(
                () -> new ServiceException("401-1", "유효하지 않은 API 키입니다.")
        );
    }

}