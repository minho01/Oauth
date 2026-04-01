# Review Note: `8404673` to `HEAD`

대상 범위:

- 시작 커밋: `8404673fe9d28e0818bfe69eaa4543c6ffcc4ff4`
- 마지막 커밋: 현재 `HEAD`

포함 커밋:

1. `f4c19f7` `41`
2. `75db08d` `43`
3. `dd7382f` `44`
4. `6b5f161` `45`
5. `1fdedd2` `47`
6. `464b3b3` `48_HttpOnly Cookie 종료`
7. `d5eadfc` `50`
8. `f17c947` `52`
9. `b5e15cf` `53`
10. `8f0c211` `54`
11. `e7504bf` `55`

이 문서는 "어떤 기능이 추가됐는지"보다 "그 코드가 왜 필요한지", "한 줄씩 무슨 역할을 하는지"를 이해하는 데 초점을 맞춘 복기 노트다.

---

## 1. 이번 범위의 큰 흐름

이번 범위는 인증 방식을 더 실전처럼 바꾸고, JWT를 직접 만들어보는 흐름까지 확장한 단계다.

핵심 흐름:

1. 로그인 후 `apiKey`를 쿠키에 담기 시작함
2. 현재 사용자 조회 로직 `Rq`가 헤더와 쿠키를 모두 처리하도록 발전함
3. 로그아웃 API가 추가됨
4. Swagger에 Bearer 인증 설명이 추가됨
5. JWT 라이브러리를 붙이고 직접 토큰을 만드는 유틸/서비스를 추가함
6. JWT 관련 테스트를 단계별로 작성함

즉, 이전까지는 "Bearer 헤더 기반 인증 사용자 조회"가 중심이었다면,
이번 범위에서는 "쿠키 기반 인증 보조 흐름 + JWT 생성 실습"까지 같이 공부한 것이다.

---

## 2. 먼저 그림부터: 현재 인증 흐름은 어떻게 동작하나

현재 인증 흐름은 크게 두 갈래다.

### 1. 헤더 방식

클라이언트가 요청 헤더에 아래처럼 보낸다.

```http
Authorization: Bearer {apiKey}
```

그러면 `Rq.getActor()`가 이 값을 읽어서 현재 사용자(`Member`)를 찾아준다.

### 2. 쿠키 방식

로그인 성공 시 서버가 응답 쿠키로 `apiKey`를 심어준다.
이후 브라우저 요청에서는 그 쿠키가 자동으로 따라가게 된다.

그러면 `Rq.getActor()`는:

- 먼저 Authorization 헤더를 보고
- 없으면 쿠키를 보고
- 거기서 `apiKey`를 찾아
- 최종적으로 현재 사용자(`Member`)를 찾는다

즉, 이번 범위의 핵심은:

- "현재 요청 사용자 찾기"를 한 군데(`Rq`)에 모으고
- 입력 출처만 헤더/쿠키 두 가지를 지원하게 만든 것

---

## 3. 커밋 흐름 복기

## `f4c19f7` `41`

핵심 변화:

- 회원 컨트롤러 기능이 조금 확장됨
- 회원 관련 테스트 보강

의미:

- 로그인 이후 사용자 관련 기능을 조금 더 현실적인 흐름으로 발전시키는 출발점

## `75db08d` `43`

핵심 변화:

- `ApiV1MemberController` 일부 단순화
- `Rq`가 더 많은 역할을 맡기 시작함

의미:

- 컨트롤러에 있던 인증 처리 일부가 공통 객체로 이동하기 시작했다.

## `dd7382f` `44`

핵심 변화:

- `Rq` 로직 보강
- 회원 컨트롤러 테스트 보강

의미:

- 인증 공통 처리의 세부 동작을 더 정확히 맞춘 단계

## `6b5f161` `45`

핵심 변화:

- `Rq` 로직이 크게 다듬어짐

의미:

- 단순 헤더 검사 수준에서 벗어나 실제 인증 흐름의 핵심 컴포넌트로 정리됨

## `1fdedd2` `47`

핵심 변화:

- 로그인 시 쿠키를 심는 흐름 추가
- 로그아웃 API 추가
- 회원 컨트롤러 테스트 확장

의미:

- 이제 로그인은 단순히 `apiKey`를 JSON으로 돌려주는 것뿐 아니라, 브라우저 친화적인 쿠키 인증 흐름도 제공하게 됨

## `464b3b3` `48_HttpOnly Cookie 종료`

핵심 변화:

- `Rq`가 쿠키도 읽도록 확장
- 게시글 컨트롤러와 Swagger 설정 보강

의미:

- "로그인 -> 쿠키 저장 -> 이후 요청 인증" 흐름이 실제 동작 가능한 형태로 완성됨

## `d5eadfc` `50`

핵심 변화:

- JWT 라이브러리 의존성 추가
- `AuthTokenService` 추가
- JWT 테스트 시작

의미:

- 단순 `apiKey` 인증을 넘어서 JWT 토큰 생성 개념을 직접 실습하기 시작한 단계

## `f17c947` `52`

핵심 변화:

- JWT 테스트 추가

의미:

- 라이브러리 사용법을 익히기 위한 단계별 실습

## `b5e15cf` `53`

핵심 변화:

- `Ut.jwt.toString()` 유틸 추가
- JWT 생성 로직을 재사용 가능한 유틸로 감쌈

의미:

- 복잡한 라이브러리 코드를 바로 서비스에 넣지 않고, 중간 추상화를 하나 만든 것

## `8f0c211` `54`

핵심 변화:

- `AuthTokenService.genAccessToken(...)` 구현
- 관련 테스트 추가

의미:

- 이제 "멤버 객체를 받아 access token 생성"이라는 서비스 레벨 기능이 생겼다

## `e7504bf` `55`

핵심 변화:

- JWT/토큰 테스트 보강

의미:

- 기능을 만들고 끝내지 않고, 작동을 검증하는 단계까지 마무리함

---

## 4. 코드 상세 설명

## 4-1. `Rq`는 왜 필요한가

파일:

- `src/main/java/com/back/global/rq/Rq.java`

이 클래스는 "현재 요청을 처리하는 데 필요한 공통 도구" 역할을 한다.

지금 이 프로젝트에서는 특히 인증과 관련된 일을 맡고 있다.

생성자 주입 필드:

- `HttpServletRequest request`
- `HttpServletResponse response`
- `MemberService memberService`

이 세 개가 필요한 이유:

- `request`: 들어온 요청에서 헤더/쿠키를 읽으려고
- `response`: 응답에 쿠키를 실어 보내려고
- `memberService`: apiKey로 실제 회원을 찾으려고

### `addCookie(String name, String value)`

하는 일:

1. `Cookie cookie = new Cookie(name, value);`
   - 새 쿠키 객체를 만든다

2. `cookie.setPath("/");`
   - 사이트 전체 경로에서 이 쿠키를 사용 가능하게 함

3. `cookie.setHttpOnly(true);`
   - 자바스크립트에서 직접 읽기 어렵게 해서 XSS 위험을 줄인다

4. `cookie.setDomain("localhost");`
   - localhost 도메인에서만 이 쿠키를 쓰도록 설정

5. `response.addCookie(cookie);`
   - 응답에 실어서 브라우저로 보낸다

의미:

- 로그인 성공 후 "브라우저가 자동으로 들고 다니는 인증값"을 만드는 역할

### `getActor()`

이 메서드가 이번 범위에서 가장 중요하다.

목표:

- "현재 요청한 사람이 누구인지" 찾아서 `Member`로 돌려주는 것

흐름을 줄 단위로 보면:

1. `String authorizationHeader = request.getHeader("Authorization");`
   - 먼저 Authorization 헤더가 있는지 확인

2. `String apiKey;`
   - 최종적으로 사용할 인증 키를 담을 변수

3. `if (authorizationHeader != null) { ... }`
   - 헤더가 있으면 헤더 방식을 사용

4. `if (!authorizationHeader.startsWith("Bearer "))`
   - Bearer 형식이 아니면 실패

5. `apiKey = authorizationHeader.replace("Bearer ", "");`
   - `Bearer ` 접두어를 떼고 실제 키만 꺼냄

6. `else { ... }`
   - 헤더가 없으면 쿠키 방식으로 내려감

7. `request.getCookies()`
   - 요청 쿠키 목록 가져오기

8. `Arrays.stream(request.getCookies())`
   - 쿠키 배열을 스트림으로 순회

9. `.filter(cookie -> cookie.getName().equals("apiKey"))`
   - 이름이 `apiKey`인 쿠키만 찾음

10. `.map(Cookie::getValue)`
   - 그 쿠키의 값만 꺼냄

11. `.findFirst().orElse("")`
   - 있으면 값, 없으면 빈 문자열

12. `if (apiKey.isBlank())`
   - 헤더도 없고 쿠키도 없어서 인증값 자체가 비어 있으면 `401-3`

13. `memberService.findByApiKey(apiKey).orElseThrow(...)`
   - 실제 회원을 찾는다
   - 없으면 `401-1`

정리하면:

- 헤더가 있으면 헤더 우선
- 없으면 쿠키 확인
- 둘 다 없거나 빈 값이면 인증 정보 없음
- 값은 있는데 회원이 없으면 잘못된 키

이 메서드 덕분에 컨트롤러는 그냥 `rq.getActor()`만 호출하면 된다.

### `deleteCookie(String name)`

로그아웃용 메서드다.

하는 일:

1. 같은 이름의 쿠키 생성
2. 값은 빈 문자열
3. 경로/도메인 동일하게 맞춤
4. `setMaxAge(0)`
   - 브라우저에게 "즉시 삭제하라"고 지시
5. 응답에 실어서 보냄

이게 중요한 이유:

- 쿠키는 서버 메모리에 있는 게 아니라 클라이언트 쪽에 저장되므로
- "삭제 응답 쿠키"를 다시 내려줘야 브라우저가 없앤다

---

## 4-2. `ApiV1MemberController`는 이번 범위에서 무엇이 달라졌나

파일:

- `src/main/java/com/back/domain/member/controller/ApiV1MemberController.java`

핵심 변화:

- 로그인 성공 시 `rq.addCookie("apiKey", actor.getApiKey())` 호출
- `DELETE /api/v1/members/logout` 추가

### `login(...)`

이전에도 하던 일:

- username으로 회원 찾기
- 비밀번호 비교
- 성공 시 `apiKey`를 응답 JSON에 담아주기

이번에 추가된 핵심:

```java
rq.addCookie("apiKey", actor.getApiKey());
```

이 한 줄의 의미:

- 브라우저 환경에서는 이제 사용자가 직접 apiKey를 저장하지 않아도 된다
- 서버가 쿠키를 내려주면 브라우저가 자동 보관하고, 다음 요청에 자동 첨부한다

즉, 로그인 성공 후:

- JSON 응답에도 apiKey가 있고
- 쿠키에도 apiKey가 심어진다

### `logout()`

흐름:

1. `rq.deleteCookie("apiKey")`
2. 브라우저가 기존 `apiKey` 쿠키를 삭제
3. 성공 메시지 반환

중요 포인트:

- 로그아웃은 DB에서 회원을 지우는 게 아니다
- 클라이언트가 들고 있던 인증 수단을 없애는 것이다

---

## 4-3. `ApiV1PostController`는 왜 거의 안 바뀐 것처럼 보이는데 중요한가

파일:

- `src/main/java/com/back/domain/post/post/controller/ApiV1PostController.java`

이 컨트롤러는 겉보기엔 많이 안 바뀌었지만, 실제로는 `Rq`의 변화 덕분에 동작 의미가 커졌다.

예를 들어:

```java
Member actor = rq.getActor();
```

이 한 줄이 이제 의미하는 것은:

- Authorization 헤더가 있으면 거기서 사용자 찾기
- 없으면 쿠키에서 찾기
- 인증 정보가 비어 있으면 예외
- 잘못된 키면 예외
- 정상 키면 회원 객체 반환

즉, 게시글 작성/수정/삭제는 이제:

- 헤더 인증도 가능
- 쿠키 인증도 가능

한 상태가 됐다.

Swagger 쪽에서 `@SecurityRequirement(name = "bearerAuth")`가 붙은 것도 중요하다.

의미:

- Swagger UI에서 이 컨트롤러의 API가 "인증이 필요한 API"처럼 보이게 한다

---

## 4-4. `SpringDoc` 설정은 왜 바뀌었나

파일:

- `src/main/java/com/back/global/springDoc/SpringDoc.java`

추가된 부분:

```java
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
```

이게 하는 일:

- Swagger/OpenAPI 문서에 "이 프로젝트는 Bearer 인증을 쓴다"는 정보를 알려준다

쉽게 말하면:

- API 문서에 자물쇠 개념을 붙이는 설정
- Swagger에서 Authorization 헤더 입력 UI를 제공할 수 있게 해준다

주의:

- 현재 실제 인증은 `apiKey` 기반이고
- 문서에는 Bearer JWT 형식처럼 보이게 설정되어 있다
- 학습 단계에서는 괜찮지만, 나중엔 "실제로 JWT를 쓸지, apiKey를 계속 쓸지"를 정리할 필요가 있다

---

## 4-5. JWT 공부 파트: `AuthTokenService`와 `Ut`

이번 범위의 후반부는 JWT를 직접 생성해보는 학습 흐름이다.

### 왜 JWT를 따로 공부했나

기존 `apiKey` 방식은 아주 단순하다.

- 서버가 회원마다 키를 하나 저장하고
- 요청마다 그 키가 맞는지 DB에서 조회

JWT는 조금 다르다.

- 토큰 자체 안에 정보가 들어간다
- 서버는 서명으로 위변조 여부를 확인할 수 있다

즉, 더 실전적인 토큰 인증 개념을 배우기 위해 JWT 실습이 추가된 것이라고 보면 된다.

### `AuthTokenService`

파일:

- `src/main/java/com/back/domain/member/service/AuthTokenService.java`

핵심 필드:

- `secretKey`
  - JWT 서명용 비밀키
- `expireTime`
  - 만료 시간

### `genAccessToken(Member member)`

하는 일:

1. `Ut.jwt.toString(...)` 호출
2. secretKey, 만료시간, payload를 넘김
3. payload에는:
   - `id`
   - `name`
4. 최종 JWT 문자열 반환

즉 이 메서드는:

- `Member` 객체를 받아
- "이 회원 정보를 담은 access token 문자열"로 바꿔주는 함수다

### `Ut.jwt.toString(...)`

파일:

- `src/main/java/com/back/standard/ut/Ut.java`

이 메서드는 JWT 생성 로직을 유틸로 감싼 것이다.

줄별 의미:

1. `ClaimsBuilder claimsBuilder = Jwts.claims();`
   - JWT payload를 쌓기 위한 객체 생성

2. `for (Map.Entry<String, Object> entry : body.entrySet())`
   - 전달받은 payload 맵을 하나씩 순회

3. `claimsBuilder.add(entry.getKey(), entry.getValue());`
   - payload 안에 값을 넣음

4. `Claims claims = claimsBuilder.build();`
   - 실제 Claims 객체 완성

5. `Date issuedAt = new Date();`
   - 발급 시각

6. `Date expiration = new Date(issuedAt.getTime() + 1000L * expireSeconds);`
   - 만료 시각

여기서 이름이 `expireSeconds`인데 실제로는 `1000L * expireSeconds`를 다시 하고 있다.
즉 변수명과 실제 의미가 조금 어긋날 수 있다.
이건 복기 포인트다.

7. `Key secretKey = Keys.hmacShaKeyFor(secret.getBytes());`
   - 비밀키 문자열을 HMAC 서명용 Key 객체로 바꿈

8. `Jwts.builder() ... .compact();`
   - JWT를 실제 문자열로 압축 생성

이 메서드의 좋은 점:

- 서비스 입장에서는 복잡한 JJWT API를 몰라도 됨
- `Ut.jwt.toString(...)`만 호출하면 됨

---

## 4-6. 테스트는 왜 이렇게 단계적으로 늘어났나

파일:

- `src/test/java/com/back/domain/post/member/service/AuthTokenServiceTest.java`

이 테스트 클래스는 JWT를 한 번에 이해시키지 않고, 단계별로 쪼개서 연습하게 만든다.

### `t1()`

- `authTokenService`가 스프링 빈으로 잘 주입되는지 확인

이 테스트가 단순해 보여도 중요한 이유:

- 최소한 스프링 컨텍스트에서 서비스 등록이 잘 되었는지 검증

### `t2()`

- JJWT 최신 방식으로 JWT를 직접 만든다
- payload: `{name="Paul", age=23}`
- 다시 파싱해서 payload가 일치하는지 검증

이 테스트의 목적:

- 라이브러리를 "원초적으로" 이해하기
- 직접 builder와 parser를 써보는 실습

### `t3()`

- 방금 배운 JWT 생성 로직을 `Ut.jwt.toString(...)`으로 감싼 뒤 사용

이 테스트의 목적:

- 유틸 메서드가 실제로 잘 작동하는지 확인

### `t4()`

- 실제 `Member` 엔티티를 가져와 `AuthTokenService.genAccessToken(member)` 호출

이 테스트의 목적:

- 단순 예제 payload가 아니라
- 실제 애플리케이션 도메인 객체를 기반으로 토큰을 생성하는 연습

즉 이 테스트 흐름은:

1. 라이브러리 이해
2. 유틸로 감싸기
3. 서비스에서 쓰기

순으로 학습 계단을 만든 것이다.

---

## 5. 이번 범위를 어렵게 느낀 이유

이번 범위가 어려운 이유는 "한 기술"이 아니라 "여러 층의 개념"이 겹쳐 있기 때문이다.

겹쳐 있는 층:

1. HTTP 요청/응답
2. 쿠키
3. Authorization 헤더
4. 현재 사용자 찾기
5. 스프링 빈 주입
6. Swagger 문서화
7. JWT 라이브러리 사용법
8. JWT 유틸 추상화
9. 서비스에서 실제 사용

즉, 단순 문법 문제가 아니라 "웹 인증 전체 그림"이 들어오기 시작하는 구간이라 어렵게 느껴지는 게 정상이다.

---

## 6. 이 범위를 쉽게 기억하는 방법

### 먼저 인증 입력 통로를 기억

- 헤더로 올 수 있다
- 쿠키로 올 수 있다

### 그 다음 공통 처리 객체를 기억

- `Rq.getActor()`

### 그 다음 로그인/로그아웃을 기억

- 로그인: 쿠키 심기
- 로그아웃: 쿠키 삭제

### 마지막으로 JWT 파트를 기억

- 직접 만들기
- 유틸로 감싸기
- 서비스로 사용하기

이 순서대로 이해하면 훨씬 덜 복잡하다.

---

## 7. 코드 분석

## 좋았던 점

### `Rq`가 인증 공통 처리의 중심이 됐다

컨트롤러가 깔끔해졌고, 인증 정책 변경도 한 곳에서 관리할 수 있게 됐다.

### JWT 학습 흐름이 단계적이다

라이브러리 직접 사용 -> 유틸 -> 서비스 순으로 학습 곡선이 좋다.

### 로그인/로그아웃이 브라우저 친화적으로 바뀌었다

쿠키를 사용하면 브라우저 기반 클라이언트에서 편하게 인증 상태를 유지할 수 있다.

## 생각해볼 점

### `HttpServletResponse response`가 컨트롤러에 직접 주입되지만 실제로는 안 쓰인다

`ApiV1MemberController` 안의 `response` 필드는 현재 불필요해 보인다.
실제 쿠키 처리는 `Rq`가 하고 있다.

### JWT와 apiKey 방향이 아직 함께 존재한다

현재 실제 인증은 `apiKey` 기반인데, JWT 생성 기능도 새로 생겼다.
학습용으로는 좋지만, 실서비스 방향으로 갈 때는 어느 쪽을 최종 인증 수단으로 삼을지 결정해야 한다.

### `expireTime` / `expireSeconds` 이름이 헷갈릴 수 있다

시간 단위가 이름과 구현에서 조금 혼동될 여지가 있다.

### 쿠키 보안 속성은 더 확장 가능하다

실무라면 아래도 같이 고민한다.

- `Secure`
- `SameSite`
- HTTPS 환경

---

## 8. 읽는 추천 순서

1. `src/main/java/com/back/global/rq/Rq.java`
2. `src/main/java/com/back/domain/member/controller/ApiV1MemberController.java`
3. `src/main/java/com/back/domain/post/post/controller/ApiV1PostController.java`
4. `src/main/java/com/back/global/springDoc/SpringDoc.java`
5. `src/main/java/com/back/standard/ut/Ut.java`
6. `src/main/java/com/back/domain/member/service/AuthTokenService.java`
7. `src/test/java/com/back/domain/post/member/controller/ApiV1MemberControllerTest.java`
8. `src/test/java/com/back/domain/post/member/service/AuthTokenServiceTest.java`

이 순서가 좋은 이유:

- 먼저 실제 인증이 어떻게 동작하는지 이해
- 그 다음 API에서 그 인증을 어떻게 쓰는지 이해
- 마지막으로 JWT 생성 학습 코드 이해

---

## 9. 스스로 설명해보면 좋은 질문

1. `Rq.getActor()`는 왜 헤더를 먼저 보고, 없을 때만 쿠키를 볼까?
2. 로그인 성공 시 왜 JSON 응답과 쿠키를 둘 다 줄 수 있을까?
3. 로그아웃은 왜 DB 수정이 아니라 쿠키 삭제로 처리할까?
4. `HttpOnly` 쿠키는 어떤 보안상 이점이 있을까?
5. `Ut.jwt.toString(...)`이 없다면 서비스 코드가 얼마나 복잡해질까?
6. 지금 프로젝트에서 실제 인증은 JWT인가, apiKey인가?
7. JWT 생성과 JWT 인증은 왜 다른 개념일까?

---

## 10. 이번 범위를 한 문장으로 정리하면

이번 범위는 "현재 사용자 인증 로직을 헤더와 쿠키까지 지원하도록 `Rq`에 공통화하고, 로그인/로그아웃 쿠키 흐름을 추가한 뒤, JWT를 직접 생성하는 유틸과 서비스를 단계적으로 실습한 구간"이다.
