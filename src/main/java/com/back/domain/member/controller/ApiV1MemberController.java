package com.back.domain.member.controller;

import com.back.domain.member.dto.MemberDto;
import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.global.exception.ServiceException;
import com.back.global.rq.Rq;
import com.back.global.rsData.RsData;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
// 회원가입/로그인 요청을 받아 서비스 계층으로 연결하는 API 진입점
public class ApiV1MemberController {
    private final MemberService memberService;
    private final Rq rq;
    private final HttpServletResponse response;

    record MemberJoinReqBody(
            String username,
            String password,
            String nickname
    ) {
    }

    record MemberJoinResBody(
            MemberDto memberDto
    ) {
    }

    // 회원가입은 검증된 요청을 서비스로 전달하고, 응답은 DTO + RsData로 감싼다.
    @PostMapping("/join")
    public RsData<MemberDto> join(@RequestBody @Valid MemberJoinReqBody reqBody) {

        Member member = memberService.join(reqBody.username, reqBody.password, reqBody.nickname);

        return new RsData(
                "회원가입이 완료되었습니다. %s님 환영합니다.".formatted(member.getName()),
                "201-1",
                new MemberJoinResBody(
                        new MemberDto(member)
                )
        );
    }
    record MemberLoginReqBody(
            String username,
            String password
    ) {
    }

    record MemberLoginResBody(
            String apiKey
    ) {
    }

    // 로그인은 username 조회 후 비밀번호를 확인하고, 성공 시 apiKey를 내려준다.
    @PostMapping("/login")
    public RsData<MemberLoginResBody> login(@RequestBody @Valid MemberLoginReqBody reqBody) {

        Member actor = memberService.findByUsername(reqBody.username).orElseThrow(
                () -> new ServiceException("401-1", "존재하지 않는 아이디입니다.")
        );

        if(!actor.getPassword().equals(reqBody.password)){
            throw new ServiceException("401-2", "비밀번호가 일치하지 않습니다.");
        }

        response.addCookie(
                new Cookie("apiKey", actor.getApiKey())
        );

        return new RsData(
                "%s님 환영합니다.".formatted(actor.getName()),
                "200-1",
                new MemberLoginResBody(actor.getApiKey())
        );
    }

    @GetMapping("/me")
    public MemberDto me() {

        Member actor = rq.getActor();
        return new MemberDto(actor);

    }
}
