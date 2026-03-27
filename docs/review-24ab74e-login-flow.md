# Review Note: `24ab74e` Login Flow

대상 커밋:

- `24ab74e45c496428279a82d9aeb720db2fb095d9`

## 한 줄 요약

이 커밋은 회원 API에 로그인 기능을 추가하고, 회원가입 경로를 `/join`으로 분리해 인증 흐름의 출발점을 만든 커밋이다.

---

## 1. 무엇이 바뀌었나

주요 변경:

- 회원가입 엔드포인트가 `POST /api/v1/members`에서 `POST /api/v1/members/join`으로 변경됨
- 로그인 엔드포인트 `POST /api/v1/members/login` 추가
- 로그인 성공 시 `apiKey`를 응답으로 반환
- 로그인 실패 케이스를 `ServiceException`으로 처리
- 회원가입 중복 테스트와 로그인 테스트 추가
- 이전에 만든 복습 문서 2개가 저장소에 추가됨

이 커밋의 실질적인 코드 기능은 두 가지다.

- 회원가입 URL을 더 명시적으로 구분
- username/password를 이용해 로그인하고, 이후 API 호출에 쓸 `apiKey`를 발급된 값으로 내려줌

---

## 2. 왜 이 변경이 중요한가

이전까지는 회원을 "가입시킬 수는 있었지만", 로그인해서 자신을 증명하는 흐름은 없었다.
이번 커밋부터는 아래 흐름이 가능해진다.

1. 회원가입
2. 로그인
3. 응답으로 `apiKey` 획득
4. 다른 API 호출 시 `apiKey` 사용

즉, 이 커밋은 게시글 작성 API에서 이미 쓰고 있던 `apiKey` 개념을 회원 도메인과 실제로 연결해주는 역할을 한다.

---

## 3. 코드가 작동하는 전체 흐름

## 회원가입 흐름

1. 클라이언트가 `POST /api/v1/members/join` 요청을 보낸다.
2. `ApiV1MemberController.join()`이 JSON 바디를 `MemberJoinReqBody`로 받는다.
3. 컨트롤러는 `memberService.join(username, password, nickname)`을 호출한다.
4. 서비스는 `findByUsername(username)`으로 기존 회원 존재 여부를 확인한다.
5. 이미 존재하면 `ServiceException("409-1", "이미 사용중인 아이디입니다.")`를 던진다.
6. 존재하지 않으면 `new Member(username, password, nickname)`으로 엔티티를 생성한다.
7. `Member` 생성자 안에서 `apiKey`가 UUID로 자동 생성된다.
8. `memberRepository.save(member)`로 저장한다.
9. 컨트롤러는 저장된 회원을 `MemberDto`로 감싼다.
10. `RsData`를 반환한다.
11. `ResponseAspect`가 `resultCode` 앞자리 `201`을 읽고 HTTP 상태코드를 `201 Created`로 맞춘다.

핵심은:

- 컨트롤러는 요청을 받고 서비스 호출
- 서비스는 중복 검사와 저장
- 엔티티는 자기 생성 시 `apiKey` 준비
- 응답은 `RsData + DTO`로 통일

## 로그인 흐름

1. 클라이언트가 `POST /api/v1/members/login` 요청을 보낸다.
2. 요청 바디의 `username`, `password`가 `MemberLoginReqBody` record에 매핑된다.
3. `ApiV1MemberController.login()`이 `memberService.findByUsername(username)` 호출
4. 회원이 없으면 `orElseThrow()`가 실행되어 `ServiceException("401-1", "존재하지 않는 아이디입니다.")` 발생
5. 회원이 있으면 `actor.getPassword().equals(reqBody.password)`로 비밀번호 일치 여부 검사
6. 비밀번호가 다르면 `ServiceException("401-2", "비밀번호가 일치하지 않습니다.")` 발생
7. 비밀번호가 맞으면 기존 회원 객체 안에 저장되어 있던 `apiKey`를 꺼낸다.
8. 컨트롤러가 `MemberLoginResBody(apiKey)`를 생성한다.
9. `RsData`에 환영 메시지와 함께 담아 반환한다.
10. `ResponseAspect`가 `200-1`을 읽고 HTTP 상태를 `200 OK`로 맞춘다.

핵심은:

- 로그인은 새 `apiKey`를 만드는 것이 아니라 기존 회원의 `apiKey`를 돌려준다.
- 인증 실패는 두 단계로 나뉜다.
- `username` 없음
- 비밀번호 불일치

---

## 4. 로그인 코드의 작동 원리

로그인 메서드의 핵심 코드는 사실상 아래 3단계다.

### 1. 사용자 조회

```java
Member actor = memberService.findByUsername(reqBody.username).orElseThrow(
        () -> new ServiceException("401-1", "존재하지 않는 아이디입니다.")
);
```

작동 원리:

- `findByUsername()`은 `Optional<Member>`를 반환한다.
- 값이 있으면 `actor`에 회원 객체가 들어간다.
- 값이 없으면 `orElseThrow()`가 예외를 던지고 메서드는 즉시 종료된다.

즉, 이 한 줄은 아래 의미를 가진다.

- "username으로 회원을 찾고"
- "없으면 실패 처리하고"
- "있으면 다음 단계로 진행한다"

### 2. 비밀번호 비교

```java
if (!actor.getPassword().equals(reqBody.password)) {
    throw new ServiceException("401-2", "비밀번호가 일치하지 않습니다.");
}
```

작동 원리:

- DB에서 가져온 회원의 실제 비밀번호와
- 요청으로 들어온 비밀번호를 문자열 비교한다.
- 다르면 예외 발생

즉, username 존재 여부와 password 일치 여부를 분리해서 검사한다.

이 구조의 장점:

- 실패 원인을 구분할 수 있다.
- 테스트를 쓰기 쉽다.
- 컨트롤러 안에서 흐름이 직선적으로 읽힌다.

### 3. 성공 응답 생성

```java
return new RsData(
        "%s님 환영합니다.".formatted(actor.getName()),
        "200-1",
        new MemberLoginResBody(actor.getApiKey())
);
```

작동 원리:

- `actor.getName()`은 내부적으로 닉네임을 반환한다.
- 메시지에는 닉네임이 들어간다.
- 응답 데이터에는 `apiKey`만 내려간다.

즉, 로그인 성공 시 서버는:

- "당신이 누구인지 확인했다"
- "앞으로 인증에 쓸 키는 이것이다"

를 같이 전달한다.

---

## 5. 이 커밋에서 배울 수 있는 개념

### 1. 인증 흐름의 최소 단위

보안 프레임워크를 붙이지 않아도, 가장 단순한 인증 흐름은 아래처럼 만들 수 있다.

- 아이디로 사용자 조회
- 비밀번호 확인
- 식별 토큰 또는 키 반환

이번 코드는 그 최소 단위를 직접 구현한 예시다.

### 2. `Optional`의 실전 사용

`findByUsername()` 결과가 없을 수 있기 때문에 `Optional`을 사용한다.
그리고 `orElseThrow()`로 실패 흐름을 자연스럽게 연결한다.

### 3. 예외를 이용한 흐름 제어

정상 흐름은 아래로 내려가고,
비정상 흐름은 예외로 빠진다.

이 패턴 덕분에 로그인 메서드는 if문이 많아지지 않고 읽기 쉬워진다.

### 4. record를 이용한 요청/응답 모델링

`MemberLoginReqBody`
`MemberLoginResBody`

처럼 작은 데이터 구조를 record로 표현해, DTO 클래스를 무겁게 만들지 않고도 깔끔하게 다룬다.

### 5. 엔드포인트 의미 분리

- `/members/join`
- `/members/login`

처럼 경로를 분리하면 역할이 더 분명해진다.

---

## 6. 테스트는 무엇을 검증하나

이 커밋에서 추가된 테스트는 단순히 "로그인이 된다"만 보는 것이 아니다.

## 중복 username 가입 테스트

검증하는 것:

- `/api/v1/members/join`로 요청했을 때
- 이미 존재하는 `username`이면
- `409 Conflict`가 반환되는지
- `resultCode`가 `409-1`인지
- 메시지가 올바른지

즉, 회원가입 서비스의 핵심 규칙이 HTTP 레벨까지 제대로 반영되는지 본다.

## 로그인 테스트

검증하는 것:

- `/api/v1/members/login` 호출 시
- 컨트롤러의 `login()` 메서드가 실행되는지
- 상태코드가 `200 OK`인지
- 환영 메시지가 회원 닉네임 기준으로 맞는지
- 응답 데이터 안에 `apiKey`가 존재하는지

즉, 로그인 성공 시 서버가 "누구인지 확인했다"는 정보와 "이후 요청에 사용할 인증 수단"을 제대로 내려주는지 검증한다.

---

## 7. 클래스별로 보면 작동 원리가 더 잘 보이는 부분

## `ApiV1MemberController`

이 커밋의 중심 클래스다.

추가된 역할:

- 회원가입 URL 정리
- 로그인 요청 처리
- 로그인 실패를 `ServiceException`으로 전달
- 로그인 성공 시 `apiKey` 응답 구성

컨트롤러 입장에서 중요한 점:

- DB 접근을 직접 하지 않는다.
- 회원 조회는 서비스에 맡긴다.
- 다만 비밀번호 비교는 아직 컨트롤러가 하고 있다.

여기서 생각해볼 점:

- 로그인 검증 책임도 나중에는 서비스로 옮길 수 있다.

## `MemberService`

이번 커밋에서는 직접 수정되진 않았지만 로그인 흐름의 시작점 역할을 한다.

중요한 역할:

- username으로 회원 찾기
- 회원가입 시 중복 username 방지

즉, 로그인 흐름에서도 컨트롤러가 회원을 찾는 데 필요한 기반 서비스를 제공한다.

## `Member`

로그인의 핵심 데이터 원천이다.

중요한 이유:

- `password` 비교 대상이 여기 있다.
- `apiKey` 반환값도 여기 있다.
- `getName()`은 닉네임을 돌려줘서 환영 메시지에 사용된다.

즉, 로그인 성공 응답의 핵심 데이터는 전부 `Member`에서 나온다.

## `GlobalExceptionHandler`

로그인 메서드에서 던진 `ServiceException`은 여기서 받아 응답으로 바뀐다.

즉, 컨트롤러는 예외만 던지고 끝나도 되고,
실제 에러 응답 포맷은 전역에서 통일된다.

## `ResponseAspect`

로그인 성공 시 `RsData("200-1", ...)`를 보고 실제 HTTP status를 200으로 설정한다.
회원가입 실패 시 `409-1`이면 409로 설정한다.

즉, 응답 본문 안의 `resultCode`와 HTTP 상태코드가 연결되는 마지막 단계다.

---

## 8. 코드 분석

## 잘된 점

### 로그인과 회원가입의 역할이 분리됐다

이전보다 API 사용 목적이 더 선명해졌다.

- 가입은 `/join`
- 인증은 `/login`

### 게시글 작성 API와 연결될 준비가 됐다

이미 게시글 작성은 `apiKey`를 받는 구조였기 때문에,
이번 로그인 API 추가로 실제 사용 흐름이 완성되기 시작했다.

### 실패 흐름이 명확하다

- 없는 아이디
- 틀린 비밀번호

를 나눠서 다루기 때문에 테스트와 디버깅이 쉬워진다.

## 더 생각해볼 점

### 비밀번호 비교가 컨트롤러에 있다

현재는 아래 두 책임이 컨트롤러 안에 있다.

- 회원 조회
- 비밀번호 검증

회원가입의 중복 검사처럼, 로그인 검증도 서비스 메서드로 옮기면 더 일관된 구조가 된다.

예를 들어 나중에는 이런 식으로 바꿀 수 있다.

- `memberService.login(username, password)`

그러면 컨트롤러는 더 얇아지고, 인증 규칙은 서비스에 모인다.

### 비밀번호가 평문이다

지금은 실습용으로 문자열 비교를 하고 있다.
실무에서는 해시 처리 후 비교해야 한다.

### 새 `apiKey`를 매번 발급하지 않는다

현재 구조는 회원 생성 시 `apiKey`를 한 번 만들고 로그인 때 그대로 반환한다.
간단하고 이해하기 쉽지만, 보안 정책이 강한 시스템이라면 로그인 시 갱신 전략도 고려할 수 있다.

### 요청 body 검증이 아직 약하다

`MemberLoginReqBody`에는 `@NotBlank` 같은 검증이 없다.
그래서 빈 문자열 입력 같은 케이스는 더 보강할 수 있다.

---

## 9. 이 커밋을 읽을 때 추천하는 순서

1. `src/main/java/com/back/domain/member/controller/ApiV1MemberController.java`
2. `src/main/java/com/back/domain/member/service/MemberService.java`
3. `src/main/java/com/back/domain/member/entity/Member.java`
4. `src/main/java/com/back/global/exception/ServiceException.java`
5. `src/main/java/com/back/global/exceptionHandler/GlobalExceptionHandler.java`
6. `src/main/java/com/back/global/aspect/ResponseAspect.java`
7. `src/test/java/com/back/domain/post/member/controller/ApiV1MemberControllerTest.java`

이 순서로 보면:

- 요청이 어디서 시작되는지
- 조회와 저장이 어디서 일어나는지
- 예외가 어떻게 응답으로 바뀌는지
- 테스트가 무엇을 보장하는지

가 자연스럽게 이어진다.

---

## 10. 스스로 설명해보면 좋은 질문

1. 로그인 성공 시 왜 `apiKey`를 응답으로 돌려줄까?
2. 로그인 실패를 `ServiceException`으로 처리하면 어떤 장점이 있을까?
3. `findByUsername(...).orElseThrow(...)`는 정확히 어떤 흐름으로 동작할까?
4. 비밀번호 검증 책임은 컨트롤러와 서비스 중 어디에 두는 것이 더 자연스러울까?
5. `RsData`와 `ResponseAspect`는 함께 어떻게 동작할까?
6. 왜 회원가입 경로를 `/members`에서 `/members/join`으로 바꿨을까?

---

## 11. 이번 커밋을 한 문장으로 정리하면

이번 커밋은 "회원가입만 가능하던 상태에서, username/password로 사용자를 확인하고 `apiKey`를 반환하는 로그인 흐름을 추가해 인증 가능한 API 구조로 한 단계 발전시킨 커밋"이다.
