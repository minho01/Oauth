# Review Note: `8dea949` to `HEAD`

대상 범위:

- 시작 커밋: `8dea9496112637e6f9a7848e8780af33e4f02fa0`
- 마지막 커밋: 현재 `HEAD`

포함 커밋:

1. `b9eff28` `70`
2. `e71bdbc` `71`
3. `e46ac30` `73`
4. `8c49205` `74`
5. `5897be2` `75`
6. `b2254aa` `76-JWT_종료\Spring security_시작`
7. `d872c0b` `77`
8. `b6b01ff` `78`
9. `c5a10a4` `79.80`
10. `86cca68` `81`
11. `8384172` `82`
12. `9f5309e` `83`
13. `76272d9` `84`

이번 범위는 "JWT 중심 학습을 마무리하고, Spring Security 기반 인증/권한 구조로 넘어가는 단계"였다.

즉,

- 토큰을 직접 다루는 코드
- 현재 사용자 판별 로직
- 관리자 권한 체크
- Security Filter Chain

이 한 번에 연결되기 시작한 구간이다.

---

## 1. 이번 범위의 큰 흐름

이번 범위를 큰 덩어리로 보면 4단계다.

1. JWT payload 구조와 현재 사용자 조회 흐름 다듬기
2. 회원 로그인/현재 사용자 테스트 보강
3. 관리자 전용 API 추가
4. Spring Security 도입 및 커스텀 인증 필터 연결

한 문장으로 줄이면:

- "직접 인증 로직을 짜던 구조에서, Spring Security가 인증 컨텍스트를 관리하는 구조로 넘어가기 시작한 구간"

---

## 2. 커밋 흐름 복습

## `b9eff28` `70`

핵심 변화:

- `Member`, `AuthTokenService`, `Rq`, `ApiV1PostController` 일부 수정

의미:

- JWT payload 안에 어떤 정보를 넣고, 현재 사용자 판단에 무엇을 쓸지 조정하기 시작한 단계

핵심 포인트:

- payload 안에 `username`, `nickname`을 넣는 방향으로 이동

## `e71bdbc` `71`

핵심 변화:

- 회원 컨트롤러 수정

의미:

- 로그인/응답 구조가 이후 Security 전환에 맞게 조금씩 정리되기 시작함

## `e46ac30` `73`

핵심 변화:

- `Rq` 로직 보강
- 회원 테스트 보강

의미:

- 잘못된 accessToken이 와도 fallback하거나 재발급하는 흐름을 준비하는 단계

## `8c49205` `74`

핵심 변화:

- 회원 테스트 추가

의미:

- accessToken 재발급/보정 시나리오를 테스트하기 시작함

## `5897be2` `75`

핵심 변화:

- `application.yaml`의 JWT 만료시간 수정

의미:

- 토큰 만료시간 단위를 더 명확히 다루기 시작함

## `b2254aa` `76-JWT_종료\Spring security_시작`

핵심 변화:

- 관리자 회원 목록 API 추가
- `MemberWithUsernameDto` 추가
- 관리자 전용 테스트 추가

의미:

- 단순 인증을 넘어서 "권한(role-like concept)"을 다루기 시작함

## `d872c0b` `77`

핵심 변화:

- `Member.isAdmin()` 추가
- 관리자 API 권한 체크 강화

의미:

- "관리자 여부 판단"이 도메인 객체 안으로 들어옴

## `b6b01ff` `78`

핵심 변화:

- 관리자 게시글 count API 추가

의미:

- 관리자 권한을 여러 도메인으로 확장하는 첫 단계

## `c5a10a4` `79.80`

핵심 변화:

- `spring-boot-starter-security` 추가
- `SecurityConfig` 추가

의미:

- 이제부터는 인증 로직을 컨트롤러/Rq만으로 처리하는 것이 아니라
- Security Filter Chain에 태워서 처리하는 방향으로 전환됨

## `86cca68` `81`

핵심 변화:

- 비밀번호 인코딩 도입
- `PasswordEncoder` 사용
- 회원가입/로그인 로직 수정

의미:

- 평문 비밀번호 비교에서 벗어나 보안적으로 더 올바른 방향으로 이동

## `8384172` `82`

핵심 변화:

- `CustomAuthenticationFilter` 추가
- `SecurityConfig`에 연결

의미:

- "내가 직접 만든 인증 필터"를 Spring Security 체인 안에서 동작시키기 시작한 핵심 커밋

## `9f5309e` `83`

핵심 변화:

- `BeanConfig` 추가
- `CustomAuthenticationFilter` 확장
- 로그 설정 보강

의미:

- Security 관련 빈 구성을 분리하고, 인증 필터 동작을 더 완성도 있게 만듦

## `76272d9` `84`

핵심 변화:

- `CustomAuthenticationFilter` 추가 보강

의미:

- 인증 필터가 실제 요청 처리의 중심이 되도록 마무리한 단계

---

## 3. 이번 범위의 핵심 개념

## 1. JWT 학습의 종료

이전 범위에서는:

- JWT 생성
- JWT 검증
- JWT payload 읽기

를 주로 배웠다.

이번 범위부터는 JWT 자체를 공부하는 비중보다,
"그 JWT를 실제 인증 시스템 안에 어떻게 넣을까?"가 더 중요해진다.

즉, JWT는 더 이상 목적이 아니라 도구가 된다.

## 2. Spring Security 시작

Spring Security는:

- 현재 요청이 인증된 요청인지
- 인증된 사용자가 누구인지
- 어떤 권한을 가졌는지

를 다루는 프레임워크다.

이번 범위에서는 기본 로그인 폼 같은 걸 쓰는 게 아니라,
프로젝트에 맞는 인증 필터를 직접 만들어 연결했다.

## 3. Filter Chain

요청이 컨트롤러에 도착하기 전에
보안 필터들이 먼저 요청을 검사한다.

쉽게 말하면:

```text
요청 -> 필터들 -> 컨트롤러
```

이번 범위에서는 `CustomAuthenticationFilter`가 그 중간에 끼어든다.

## 4. Authentication / SecurityContext

Spring Security에서는 현재 로그인한 사용자를
`SecurityContextHolder` 안에 넣어둔다.

그러면 이후 코드에서는
"현재 인증된 사용자"를 SecurityContext 기준으로 볼 수 있다.

이번 범위의 핵심은:

- `Rq.getActor()`만 쓰던 구조에서
- SecurityContext까지 채우는 구조로 넘어가기 시작한 것

## 5. PasswordEncoder

이전:

- 평문 비밀번호 비교

현재:

- 회원가입 시 비밀번호를 암호화해서 저장
- 로그인 시 `matches()`로 비교

이건 매우 중요한 변화다.
실무에서는 평문 비밀번호 저장이 절대 안 된다.

## 6. 관리자 권한

이번 범위에서는 복잡한 Role 시스템 대신
`Member.isAdmin()`으로 관리자 여부를 판단한다.

즉,

- 관리자 전용 API
- 일반 사용자 차단

구조를 간단하게 먼저 익히는 단계다.

---

## 4. 코드 상세 설명

## 4-1. `Member`는 왜 바뀌었나

파일:

- `src/main/java/com/back/domain/member/entity/Member.java`

중요한 부분:

### `isAdmin()`

```java
public boolean isAdmin() {
    return "admin".equals(username);
}
```

의미:

- username이 `admin`이면 관리자라고 간주

왜 이렇게 했나:

- 실습 단계에서 가장 단순한 권한 모델을 만들기 위해

장점:

- 이해가 쉽다
- 테스트 쓰기 쉽다

한계:

- 실무용 권한 모델은 아니다
- 나중에는 role 테이블이나 authority 개념으로 확장 가능

### 생성자 변화

JWT payload 기반 사용자 표현과 Security 연동 때문에
현재 사용자 표현용 생성자 흐름이 중요해졌다.

---

## 4-2. `MemberService`는 왜 중요해졌나

파일:

- `src/main/java/com/back/domain/member/service/MemberService.java`

이번 범위의 핵심 변화:

### `PasswordEncoder` 주입

```java
private final PasswordEncoder passwordEncoder;
```

의미:

- 비밀번호를 그냥 문자열로 저장/비교하지 않고
- 보안용 인코더를 사용한다

### 회원가입 시 암호화

```java
passwordEncoder.encode(password)
```

의미:

- DB에 저장되는 값은 원본 비밀번호가 아니라 암호화된 문자열

### `checkPassword(...)`

```java
passwordEncoder.matches(inputPassword, rawPassword)
```

주의:

- 이름만 보면 `rawPassword`가 원본처럼 보이지만,
실제론 DB에 저장된 암호화 비밀번호를 비교하는 흐름이다

이 메서드가 중요한 이유:

- 비밀번호 검증 규칙이 컨트롤러에 흩어지지 않고 서비스에 모인다

### `findAll()`

관리자 회원 목록 API를 위해 추가된 메서드

즉 이번 범위의 `MemberService`는:

- 회원가입
- 로그인 비밀번호 검증
- 토큰 관련 기능
- 관리자 조회

까지 범위가 확장되었다.

---

## 4-3. `ApiV1MemberController`는 무엇이 바뀌었나

파일:

- `src/main/java/com/back/domain/member/controller/ApiV1MemberController.java`

가장 중요한 변화:

- 로그인 시 accessToken 응답 유지
- 비밀번호 검증 로직이 서비스 쪽과 더 자연스럽게 연결됨

핵심 이해 포인트:

- 이 시점부터 로그인은 단순 컨트롤러 로직이 아니라
- 보안 구성과 연결될 준비를 하는 단계가 된다

---

## 4-4. 관리자 전용 API는 왜 추가됐나

### `ApiV1AdmMemberController`

파일:

- `src/main/java/com/back/domain/member/controller/ApiV1AdmMemberController.java`

하는 일:

- `/api/v1/adm/members`
- 전체 회원 목록 조회

흐름:

1. `rq.getActor()`로 현재 사용자 조회
2. `actor.isAdmin()` 확인
3. 아니면 `403-1`
4. 맞으면 회원 목록 반환

배울 개념:

- 관리자 전용 엔드포인트
- 인증과 권한은 다르다

### `MemberWithUsernameDto`

파일:

- `src/main/java/com/back/domain/member/dto/MemberWithUsernameDto.java`

왜 필요한가:

- 관리자 화면/API는 일반 사용자보다 더 많은 정보를 볼 수 있다
- 기존 `MemberDto`는 `username`이 없으니, 관리자 전용 DTO를 따로 둔 것

이건 DTO 설계에서 아주 중요한 감각이다.

- API 대상에 따라 DTO를 다르게 만들 수 있다

### `ApiV1AdmPostController`

파일:

- `src/main/java/com/back/domain/post/controller/ApiV1AdmPostController.java`

하는 일:

- `/api/v1/adm/posts/count`
- 전체 게시글 개수 조회

흐름:

1. 현재 사용자 조회
2. 관리자 여부 확인
3. 맞으면 count 반환

이 API의 의미:

- 관리자 전용 통계/집계성 API의 시작점

---

## 4-5. `SecurityConfig`는 무슨 역할인가

파일:

- `src/main/java/com/back/global/security/SecurityConfig.java`

이 클래스는 Spring Security의 전체 큰 설정을 잡는다.

### `@EnableWebSecurity`

의미:

- 웹 보안 설정 활성화

### `filterChain(HttpSecurity http)`

이 메서드가 진짜 핵심이다.

#### 요청 권한 설정

```java
.authorizeHttpRequests(...)
```

현재 설정 의미:

- `/favicon.ico`, `/h2-console/**` 허용
- `/**`도 일단 허용

주의:

- 현재는 학습 단계라 전체 permitAll 성격이 강하다
- 하지만 커스텀 필터를 끼워 인증 정보를 넣는 구조를 배우는 게 핵심

#### CSRF 비활성화

```java
.csrf(csrf -> csrf.disable())
```

의미:

- REST API 테스트/학습 환경에서 편하게 쓰기 위해 CSRF를 꺼둠

#### H2 Console frame 허용

```java
.headers(... SAMEORIGIN)
```

의미:

- H2 Console이 iframe 관련 제약 때문에 막히지 않게 함

#### 커스텀 필터 등록

```java
.addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
```

이게 제일 중요하다.

뜻:

- 기본 username/password 인증 필터보다 먼저
- 내가 만든 `CustomAuthenticationFilter`를 실행하겠다

즉, 이번 범위에서 보안의 중심은 이 필터다.

---

## 4-6. `BeanConfig`는 왜 따로 만들었나

파일:

- `src/main/java/com/back/global/security/BeanConfig.java`

여기서는 `PasswordEncoder` 빈을 등록한다.

```java
return new BCryptPasswordEncoder();
```

의미:

- 이제 프로젝트 전체에서 같은 비밀번호 인코더를 주입받아 사용할 수 있다

왜 따로 빼는가:

- SecurityConfig 안에 다 넣지 않고
- 공용 빈 설정을 분리하면 관리가 편하다

---

## 4-7. `CustomAuthenticationFilter`가 제일 중요하다

파일:

- `src/main/java/com/back/global/security/CustomAuthenticationFilter.java`

이번 범위 전체에서 가장 중요하고 가장 어려운 클래스다.

이 필터의 목표:

- API 요청이 들어올 때
- 현재 요청 사용자를 해석하고
- Spring Security의 인증 컨텍스트에 넣는 것

### 클래스 구조

```java
extends OncePerRequestFilter
```

의미:

- 요청당 한 번만 실행되는 필터

### `doFilterInternal(...)`

이 메서드가 실제 인증 흐름이다.

#### 1. API 요청만 처리

```java
if(!request.getRequestURI().startsWith("/api/")) ...
```

의미:

- API가 아닌 요청은 그냥 통과

#### 2. 회원가입/로그인은 예외 처리

```java
if(List.of("/api/v1/members/join", "/api/v1/members/login").contains(...))
```

왜?

- 이 요청들은 아직 로그인 전 요청이니까
- 인증 필터가 막으면 안 된다

#### 3. 인증 정보 추출

헤더 또는 쿠키에서:

- `apiKey`
- `accessToken`

을 읽는다.

이 부분은 이전 `Rq` 흐름과 비슷하지만,
이제 그 결과를 SecurityContext까지 연결한다는 점이 다르다.

#### 4. accessToken 우선 시도

토큰이 있으면 payload를 읽는다.

payload에서 꺼내는 값:

- `id`
- `username`
- `nickname`

그리고:

```java
member = new Member(id, username, nickname);
```

으로 현재 사용자 객체를 만든다.

즉, DB 조회 없이도 현재 사용자를 빠르게 표현 가능

#### 5. fallback: apiKey로 조회

accessToken이 없거나 유효하지 않으면
기존 방식대로 `apiKey`로 회원을 찾는다.

#### 6. accessToken 재발급

```java
if (isAccessTokenExists && !isAccessTokenValid) {
    String newAccessToken = memberService.genAccessToken(member);
    rq.addCookie("accessToken", newAccessToken);
    rq.setHeader("accessToken", newAccessToken);
}
```

의미:

- accessToken은 잘못됐지만
- apiKey는 유효해서 사용자를 찾았다면
- 새 accessToken을 만들어서 응답에 실어준다

이건 이번 범위에서 매우 중요한 포인트다.

즉:

- "accessToken이 만료/오류여도 apiKey가 살아 있으면 다시 발급 가능"

학습 포인트:

- accessToken / refresh-like fallback 개념의 아주 단순화된 버전

#### 7. SecurityContext 저장

```java
UserDetails user = new User(...)
Authentication authentication = new UsernamePasswordAuthenticationToken(...)
SecurityContextHolder.getContext().setAuthentication(authentication);
```

이 부분이 Spring Security의 핵심이다.

뜻:

- 현재 요청의 인증된 사용자 정보를
- SecurityContext에 저장한다

그러면 이후 Spring Security는
"이 요청은 인증된 요청"이라고 이해할 수 있다.

---

## 4-8. `Rq`는 어떻게 바뀌었나

파일:

- `src/main/java/com/back/global/rq/Rq.java`

이번 범위에서는 `Rq`도 보조 역할을 한다.

변화:

- 헤더/쿠키 읽기 보조 메서드 노출
- 필터가 쓰기 좋은 구조로 조금 조정

즉, 이전에는 `Rq`가 인증의 중심이었다면,
이제는:

- 인증 필터가 중심
- `Rq`는 요청 보조 도구

로 역할이 조금 이동한다.

이게 바로 "JWT 종료 / Spring Security 시작"의 감각이다.

---

## 5. 테스트는 무엇을 보여주나

## `ApiV1MemberControllerTest`

보여주는 것:

- 로그인 성공 시 `apiKey`, `accessToken` 둘 다 내려감
- 쿠키도 함께 세팅됨
- 잘못된 accessToken + 올바른 apiKey 상황에서 보정 가능성 확인

## `ApiV1AdmMemberControllerTest`

보여주는 것:

- 관리자는 회원 목록 조회 가능
- 일반 사용자는 403

## `ApiV1AdmPostControllerTest`

보여주는 것:

- 관리자만 게시글 count 조회 가능
- 일반 사용자는 403

즉, 테스트는 이번 범위에서 아래를 증명한다.

- 인증이 된다
- 관리자 권한 체크가 된다
- Security 전환 이후에도 흐름이 맞다

---

## 6. 이번 범위가 어려운 이유

이번 범위는 아래 개념이 동시에 들어온다.

1. JWT payload 구조 변경
2. accessToken / apiKey 이중 인증 정보
3. 관리자 권한 개념
4. 비밀번호 암호화
5. Spring Security Filter Chain
6. CustomAuthenticationFilter
7. SecurityContextHolder

즉, 단순 CRUD가 아니라 "보안 아키텍처"를 만지는 구간이라 어렵게 느껴지는 게 자연스럽다.

---

## 7. 쉽게 기억하는 방법

### 1. JWT 파트는 마무리 단계

- accessToken 만들고 읽는 도구

### 2. Security 파트는 시작 단계

- 요청이 컨트롤러 전에 필터를 지난다

### 3. 필터가 하는 핵심

- 사용자 찾기
- 토큰 보정
- SecurityContext 저장

### 4. 관리자 API 핵심

- `actor.isAdmin()` 아니면 403

### 5. 비밀번호 핵심

- 저장은 `encode`
- 비교는 `matches`

---

## 8. 읽는 추천 순서

1. `src/main/java/com/back/global/security/SecurityConfig.java`
2. `src/main/java/com/back/global/security/CustomAuthenticationFilter.java`
3. `src/main/java/com/back/global/security/BeanConfig.java`
4. `src/main/java/com/back/domain/member/service/MemberService.java`
5. `src/main/java/com/back/domain/member/entity/Member.java`
6. `src/main/java/com/back/domain/member/controller/ApiV1AdmMemberController.java`
7. `src/main/java/com/back/domain/post/controller/ApiV1AdmPostController.java`
8. `src/test/java/com/back/domain/member/controller/ApiV1AdmMemberControllerTest.java`
9. `src/test/java/com/back/domain/post/controller/ApiV1AdmPostControllerTest.java`
10. `src/test/java/com/back/domain/member/controller/ApiV1MemberControllerTest.java`

이 순서가 좋은 이유:

- 먼저 보안 구조
- 그다음 현재 사용자/비밀번호/권한
- 마지막으로 관리자 API와 테스트

순으로 이해되기 때문

---

## 9. 스스로 설명해보면 좋은 질문

1. 왜 `Rq`만으로 인증하지 않고 Security Filter로 넘어갔을까?
2. `CustomAuthenticationFilter`는 왜 `OncePerRequestFilter`를 상속할까?
3. 왜 로그인/회원가입 요청은 필터에서 그냥 통과시킬까?
4. accessToken이 잘못됐는데 apiKey는 맞을 때 왜 새 토큰을 발급할 수 있을까?
5. `SecurityContextHolder`에 인증 정보를 넣는 이유는 무엇일까?
6. `PasswordEncoder.encode()`와 `matches()`는 각각 언제 쓰일까?
7. 관리자 권한을 `isAdmin()`으로 단순화한 이유는 무엇일까?

---

## 10. 이번 범위를 한 문장으로 정리하면

이번 범위는 "JWT 기반 현재 사용자 해석 흐름을 마무리하면서, Spring Security와 커스텀 인증 필터를 도입해 인증 컨텍스트를 관리하고, 관리자 전용 API와 비밀번호 암호화까지 포함한 보안 구조의 기초를 세운 단계"였다.
