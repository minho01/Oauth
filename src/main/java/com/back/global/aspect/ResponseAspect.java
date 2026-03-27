package com.back.global.aspect;

import com.back.global.rsData.RsData;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
// RsData의 resultCode를 실제 HTTP 상태코드로 반영하는 공통 후처리 AOP
public class ResponseAspect {

    private final HttpServletResponse response;

    @Around("""
            (
                within
                (
                    @org.springframework.web.bind.annotation.RestController *
                )
                &&
                (
                    @annotation(org.springframework.web.bind.annotation.GetMapping)
                    ||
                    @annotation(org.springframework.web.bind.annotation.PostMapping)
                    ||
                    @annotation(org.springframework.web.bind.annotation.PutMapping)
                    ||
                    @annotation(org.springframework.web.bind.annotation.DeleteMapping)
                )
            )
            ||
            @annotation(org.springframework.web.bind.annotation.ResponseBody)
            """)
    public Object responseAspect(ProceedingJoinPoint joinPoint) throws Throwable {


        System.out.println("ResponseAspec 전처리");

        Object rst = joinPoint.proceed(); // 실제 수행 메서드

        System.out.println("ResponseAspec 후처리");
        if(rst instanceof RsData rsData) {
            int statusCode = rsData.getStatusCode();
            response.setStatus(statusCode);
        }

        return rst;
    }

}
