# Latest Commit Study Note

대상 커밋: `1887541d706140d314df2d0a710e8ff0d7711cff`

## 한 줄 요약

회원 가입 시 `username` 중복 검사를 `Controller`에서 `Service`로 이동한 커밋이다.

## 무엇이 바뀌었나

변경 파일:

- `src/main/java/com/back/domain/member/controller/ApiV1MemberController.java`
- `src/main/java/com/back/domain/member/service/MemberService.java`

변경 내용:

- 컨트롤러의 중복 회원 검사 로직 삭제
- 서비스의 `join()` 메서드 안에 중복 회원 검사 로직 추가

## 변경 전 흐름

1. 컨트롤러가 요청을 받는다.
2. 컨트롤러가 `findByUsername()`으로 중복 여부를 직접 검사한다.
3. 중복이 아니면 서비스의 `join()`을 호출한다.
4. 서비스는 단순 저장만 수행한다.

이 구조의 문제:

- 회원 가입 규칙이 컨트롤러에 박혀 있다.
- 다른 곳에서 `memberService.join()`을 호출하면 중복 검사 없이 저장될 수 있다.
- 비즈니스 규칙이 여러 진입점에 흩어질 위험이 있다.

## 변경 후 흐름

1. 컨트롤러가 요청을 받는다.
2. 컨트롤러는 `memberService.join()`만 호출한다.
3. 서비스가 내부에서 `findByUsername()`으로 중복 여부를 검사한다.
4. 중복이면 `ServiceException("409-1", "...")`를 던진다.
5. 중복이 아니면 `Member`를 생성하고 저장한다.

## 왜 더 좋아졌나

### 1. 비즈니스 규칙이 서비스에 모인다

`username` 중복 검사는 HTTP 요청 처리 규칙이 아니라 회원 가입 자체의 규칙이다.
그래서 컨트롤러보다 서비스 계층에 있는 편이 자연스럽다.

### 2. 재사용성이 좋아진다

이제 웹 컨트롤러 말고 다른 코드가 `memberService.join()`을 호출해도 중복 검사가 동일하게 적용된다.

### 3. 컨트롤러가 더 얇아진다

컨트롤러는 요청과 응답에 집중하고, 핵심 규칙은 서비스가 담당하게 된다.
이런 구조를 흔히 "컨트롤러는 얇게, 서비스는 두껍게"라고 표현한다.

## 코드 관점에서 읽어볼 포인트

### 컨트롤러에서 제거된 코드

```java
memberService.findByUsername(reqBody.username).ifPresent(
        m -> {
            throw new ServiceException("409-1", "이미 사용중인 아이디입니다.");
        }
);
```

이제 이 책임은 서비스로 이동했다.

### 서비스에 추가된 코드

```java
findByUsername(username).ifPresent(
        m -> {
            throw new ServiceException("409-1", "이미 사용중인 아이디입니다.");
        }
);
```

`join()`이 호출되는 모든 경로에서 동일한 규칙을 보장한다는 점이 핵심이다.

## 이 커밋으로 배울 수 있는 개념

- 계층 분리
- 비즈니스 로직의 위치
- 중복 검증 책임 배치
- 예외를 통한 흐름 제어
- 서비스 중심 설계

## 공부할 때 스스로 답해보면 좋은 질문

1. `username` 중복 검사는 왜 컨트롤러보다 서비스에 더 어울릴까?
2. 지금 구조에서 `MemberService.join()`을 다른 곳에서 호출하면 어떤 장점이 생길까?
3. 이런 검증을 서비스에만 두는 것과 DB 유니크 제약만 믿는 것은 어떻게 다를까?
4. 동시에 두 요청이 들어오면 서비스 검증만으로 충분할까?
5. 왜 `ServiceException`을 던지고, 이 예외는 어디서 HTTP 응답으로 바뀌는가?

## 추가로 같이 보면 좋은 파일

- `src/main/java/com/back/domain/member/controller/ApiV1MemberController.java`
- `src/main/java/com/back/domain/member/service/MemberService.java`
- `src/main/java/com/back/global/exception/ServiceException.java`
- `src/main/java/com/back/global/exceptionHandler/GlobalExceptionHandler.java`

## 실무 관점에서 한 단계 더 생각할 점

이 커밋 방향은 좋다. 다만 완전히 안전하다고 보기는 어렵다.

- 서비스에서 먼저 조회하고 저장하는 방식은 동시성 상황에서 중복 가입이 완전히 막히지 않을 수 있다.
- 최종 방어선으로는 DB의 `unique` 제약도 함께 필요하다.
- 그리고 DB 제약 위반이 발생했을 때도 적절한 예외 변환 처리가 있으면 더 견고해진다.

즉, 가장 좋은 방향은 보통 아래 두 가지를 같이 가져간다.

- 서비스에서 미리 검증해서 친절한 에러 메시지 제공
- DB 제약으로 최종 무결성 보장

## 이번 커밋에 대한 평가

좋아진 점:

- 책임 분리가 더 자연스러워졌다.
- 회원 가입 규칙이 서비스 계층에 모였다.
- 컨트롤러가 단순해졌다.

아쉬운 점:

- 동시성까지 고려한 최종 방어는 아직 부족할 수 있다.
- 관련 테스트가 함께 보강되면 더 좋은 커밋이 된다.
