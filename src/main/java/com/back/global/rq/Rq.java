package com.back.global.rq;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.global.exception.ServiceException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static io.swagger.v3.core.util.AnnotationsUtils.getHeader;

@Component
@RequiredArgsConstructor
public class Rq {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final MemberService memberService;


    public Member getActor() {
        return new Member(3, "user1", "유저1");
    }

    public void setHeader(String name, String value) {
        response.setHeader(name, value);
    }

    public String getHeader(String name, String defaultValue) {
        return Optional
                .ofNullable(request.getHeader(name))
                .filter(headerValue -> !headerValue.isBlank())
                .orElse(defaultValue);
    }

    public String getCookieValue(String name, String defaultValue) {
        return Optional
                .ofNullable(request.getCookies())
                .flatMap(
                        cookies ->
                                Arrays.stream(cookies)
                                        .filter(cookie -> cookie.getName().equals(name))
                                        .map(Cookie::getValue)
                                        .filter(value -> !value.isBlank())
                                        .findFirst()
                )
                .orElse(defaultValue);
    }

    public void deleteCookie(String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setDomain("localhost");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }

    public void addCookie(String name, String value) {

        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setDomain("localhost");

        response.addCookie(
                cookie
        );
    }
}