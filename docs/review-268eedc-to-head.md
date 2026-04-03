# Review Note: `268eedc` to `HEAD`

대상 범위:

- 시작 커밋: `268eedc1fb62135e50c041031ecfbb9a389bd117`
- 마지막 커밋: 현재 `HEAD`

포함 커밋:

1. `7a60af6` `86`
2. `34333dd` `87,88,89`
3. `d288061` `90`
4. `1ce9346` `90-2`
5. `60e6c87` `91,92`
6. `7695ea6` `93`
7. `9fc1918` `94`

이번 범위는 "Spring Security를 단순히 붙여놓은 상태"에서 한 단계 더 나아가,
"권한 체크를 컨트롤러 밖으로 밀어내고, Security가 인증/인가를 맡게 만드는 과정"이었다.

즉 핵심은 이거다.

- 인증은 필터에서 처리
- 사용자 정보는 `SecurityContext`에 저장
- 관리자 권한은 `ROLE_ADMIN` 같은 권한 정보로 판단
- 컨트롤러는 비즈니스 로직에 더 집중

---

## 1. 전체 흐름 한눈에 보기

이번 범위를 큰 줄기로 정리하면 4단계다.

1. `CustomAuthenticationFilter`를 더 안정적으로 다듬음
2. Security 설정에서 인증/인가 규칙을 구체화함
3. `UserDetailsService`, `SecurityUser`, `GrantedAuthority` 개념 도입
4. 관리자 권한 체크를 컨트롤러에서 Security로 이동

한 문장으로 말하면:

- "직접 if문으로 권한을 체크하던 구조에서, Spring Security가 권한을 판별하는 구조로 전환한 단계"

---

## 2. 커밋 흐름 복습

## `7a60af6` `86`

핵심 변화:

- `CustomAuthenticationFilter` 보강

의미:

- 필터가 단순 토큰 파서가 아니라, 실제 인증 처리기의 형태를 갖추기 시작함

## `34333dd` `87,88,89`

핵심 변화:

- `CustomAuthenticationFilter`와 `SecurityConfig` 정리
- 댓글 테스트 일부 조정

의미:

- Security 체인과 실제 API 테스트가 조금 더 자연스럽게 맞물리도록 다듬은 단계

## `d288061` `90`

핵심 변화:

- `SecurityConfig`에 인가 규칙 보강
- 게시글 테스트 정리

의미:

- "어떤 요청은 인증 없이 허용하고"
- "어떤 요청은 인증이 필요하고"
- "어떤 요청은 관리자 권한이 필요하다"

를 Security 설정에서 더 명확하게 표현하기 시작함

## `1ce9346` `90-2`

핵심 변화:

- `CustomUserDetailService` 추가

의미:

- Spring Security의 사용자 조회 표준 인터페이스를 프로젝트에 연결

배울 점:

- 이제 사용자 조회가 단순 서비스 메서드 호출 수준을 넘어서
- Spring Security가 이해하는 방식으로 연결되기 시작했다

## `60e6c87` `91,92`

핵심 변화:

- 관리자 컨트롤러 안의 직접 권한 체크 제거
- `Member.getAuthorities()` 추가
- Security 설정과 사용자 조회 구조 보강

의미:

- 관리자 여부를 컨트롤러의 `if (!actor.isAdmin())`로 보지 않고
- Security 권한으로 해결하는 방향으로 이동

이게 이번 범위에서 가장 중요한 포인트다.

## `7695ea6` `93`

핵심 변화:

- 보안 필터/설정 관련 보강

의미:

- Security 기반 인증 흐름을 실제 요청에 더 잘 맞게 다듬는 중간 단계

## `9fc1918` `94`

핵심 변화:

- `Rq` 보조 기능 보강
- `SecurityConfig` 인가/예외 처리 보강

의미:

- 최종적으로 Spring Security가 실패 응답까지 더 일관되게 내려주도록 정리한 단계

---

## 3. 이번 범위의 핵심 개념

## 1. 인증(Authentication)과 인가(Authorization)

이 두 개는 꼭 분리해서 이해해야 한다.

### 인증

- "너 누구냐?"
- 로그인했는가?
- 토큰이 유효한가?

이번 코드에서는:

- `CustomAuthenticationFilter`
- `SecurityContextHolder`

가 주로 담당한다.

### 인가

- "그 행동을 해도 되냐?"
- 관리자 권한이 있냐?

이번 코드에서는:

- `ROLE_ADMIN`
- `hasRole("ADMIN")`

이 담당한다.

즉:

- 인증은 사용자 식별
- 인가는 권한 판단

이다.

## 2. SecurityContext

Spring Security는 현재 로그인한 사용자를
`SecurityContextHolder` 안에 보관한다.

이 뜻은:

- 컨트롤러가 직접 매번 인증하지 않아도 되고
- Security가 "이 요청은 누구 요청"인지 기억하게 된다는 것

이번 범위는 바로 이 구조를 쓰기 시작한 단계다.

## 3. UserDetails / UserDetailsService

Spring Security는 사용자 정보를 다룰 때
자기 표준 인터페이스를 좋아한다.

### `UserDetails`

- Spring Security가 이해하는 사용자 표현 객체

### `UserDetailsService`

- username으로 사용자를 찾아 `UserDetails`로 바꿔주는 서비스

이번 범위에서는:

- `SecurityUser`
- `CustomUserDetailService`

가 이 역할을 맡는다.

## 4. GrantedAuthority

Spring Security는 권한을 문자열 리스트처럼 본다.

예:

- `ROLE_ADMIN`
- `ROLE_USER`

이번 범위에서 `Member.getAuthorities()`가 생긴 이유가 바로 이것이다.

즉:

- 관리자인지 아닌지 단순 boolean으로 끝내지 않고
- Security가 이해하는 권한 표현으로 바꿔주는 것

## 5. 컨트롤러 권한 체크 vs Security 권한 체크

이전 방식:

```java
if (!actor.isAdmin()) {
    throw new ServiceException(...)
}
```

현재 방향:

- 컨트롤러는 if문을 줄인다
- Security 설정에서 `hasRole("ADMIN")`으로 막는다

장점:

- 권한 정책이 한 곳(SecurityConfig)에 모인다
- 컨트롤러가 더 깔끔해진다
- 정책 변경이 쉬워진다

---

## 4. 코드 상세 설명

## 4-1. `Member`는 왜 중요해졌나

파일:

- `src/main/java/com/back/domain/member/entity/Member.java`

이번 범위에서 가장 중요한 추가:

### `getAuthorities()`

```java
public List<SimpleGrantedAuthority> getAuthorities() {
    if (isAdmin()) {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    } else {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
```

이 코드의 의미:

- `Member` 객체를
- Spring Security가 이해하는 "권한 보유 사용자"로 바꾸는 재료를 제공

쉽게 말하면:

- `admin`이면 `ROLE_ADMIN`
- 그 외는 `ROLE_USER`

이렇게 권한 이름을 붙여주는 것

왜 중요한가:

- 이제 관리자 API 보호를 `if(isAdmin())` 대신
- Security 권한으로 표현할 수 있게 되기 때문

### `isAdmin()`

이 메서드는 여전히 남아 있지만,
중심 역할은 점점 줄어든다.

이유:

- Security의 권한 시스템이 더 중심이 되기 때문

---

## 4-2. `SecurityUser`는 무슨 역할인가

파일:

- `src/main/java/com/back/global/security/SecurityUser.java`

이 클래스는 Spring Security의 `User`를 상속한다.

왜 만들었나:

- 기본 `User`는 username/password/authorities 중심인데
- 우리는 여기에 `id`, `nickname`도 같이 들고 다니고 싶기 때문

즉 이 클래스는:

- Spring Security가 이해할 수 있으면서
- 프로젝트에 필요한 추가 정보도 가진 사용자 객체

라고 보면 된다.

핵심 필드:

- `id`
- `nickname`

---

## 4-3. `CustomUserDetailService`는 왜 필요한가

파일:

- `src/main/java/com/back/global/security/CustomUserDetailService.java`

이 클래스는 `UserDetailsService` 구현체다.

핵심 메서드:

```java
loadUserByUsername(String username)
```

하는 일:

1. `memberService.findByUsername(username)`로 실제 회원 찾기
2. 없으면 `UsernameNotFoundException`
3. 있으면 `SecurityUser`로 변환해서 반환

이 의미는:

- Spring Security가 username만 가지고도
- 우리 프로젝트의 사용자 객체를 얻을 수 있게 만든 것

즉, Spring Security와 프로젝트 도메인을 연결하는 어댑터 같은 역할이다.

---

## 4-4. `CustomAuthenticationFilter`는 무엇이 달라졌나

파일:

- `src/main/java/com/back/global/security/CustomAuthenticationFilter.java`

이번 범위에서 이 필터는 더 정교해졌다.

### 전체 역할

요청이 들어오면:

1. 인증 정보 추출
2. 사용자 해석
3. `Authentication` 객체 생성
4. `SecurityContextHolder`에 저장
5. 실패 시 JSON 에러 응답 반환

### `doFilterInternal(...)`

이번 범위에서 중요한 변화:

- `try-catch`로 `ServiceException`을 직접 잡음
- 필터 안에서 JSON 에러 응답을 작성

왜 중요한가:

- 필터는 컨트롤러 밖에서 동작하므로
- `@ControllerAdvice`가 항상 자연스럽게 처리해주지 않는다

즉, 필터 단계 예외는 필터에서 직접 응답 포맷을 맞춰주는 게 자연스럽다.

### `authenticate(...)`

핵심 흐름:

1. API가 아닌 요청은 패스
2. 회원가입/로그인 요청은 패스
3. 헤더나 쿠키에서 인증 정보 읽기
4. accessToken, apiKey 해석
5. 현재 사용자 구하기
6. accessToken이 이상하면 새로 발급 가능
7. `SecurityUser` 생성
8. `UsernamePasswordAuthenticationToken` 생성
9. `SecurityContextHolder`에 저장

중요한 차이:

이전에는 "현재 사용자 알아내기" 자체가 목적이었다면
이제는 "현재 사용자를 Spring Security 컨텍스트에 넣는 것"이 목적이다.

### `SecurityUser`를 넣는 이유

예전에는 그냥 `new User(...)`를 넣었다면,
지금은 `new SecurityUser(...)`를 넣는다.

이유:

- 권한 정보
- id
- nickname

같은 프로젝트 전용 정보까지 같이 실어주기 위해서다.

---

## 4-5. `SecurityConfig`가 이번 범위의 정책 본부다

파일:

- `src/main/java/com/back/global/security/SecurityConfig.java`

이 클래스는 "누가 어디까지 들어갈 수 있는가"를 선언적으로 적는 곳이다.

### 공개 API

```java
.requestMatchers(HttpMethod.GET, "/api/*/posts", ...).permitAll()
.requestMatchers(HttpMethod.POST, "/api/v1/members/login", "/api/v1/members/join").permitAll()
.requestMatchers(HttpMethod.DELETE, "/api/v1/members/logout").permitAll()
```

의미:

- 게시글/댓글 조회
- 회원가입/로그인/로그아웃

같은 요청은 인증 없이 허용

### 관리자 API

```java
.requestMatchers("/api/v1/adm/**").hasRole("ADMIN")
```

이게 이번 범위에서 제일 중요한 한 줄이다.

의미:

- `/api/v1/adm/**` 아래는
- `ROLE_ADMIN` 권한이 있는 사용자만 접근 가능

결과:

- 관리자 컨트롤러 안에서 직접 권한 체크 코드를 뺄 수 있음

### 인증 필요 API

```java
.requestMatchers("/api/*/**").authenticated()
```

의미:

- 공개 API/관리자 API 규칙에 걸리지 않은 나머지 API는
- 로그인된 사용자만 접근 가능

### 예외 처리

```java
.authenticationEntryPoint(...)
.accessDeniedHandler(...)
```

이 부분도 매우 중요하다.

의미:

- 인증이 안 된 사용자가 들어오면 401 JSON
- 인증은 됐지만 권한이 없으면 403 JSON

즉, Security가 실패 응답까지 직접 내려주기 시작했다.

이건 이전 범위보다 더 발전한 점이다.

---

## 4-6. 관리자 컨트롤러는 왜 코드가 줄었나

### `ApiV1AdmMemberController`

이전:

- `rq.getActor()`
- `if (!actor.isAdmin()) throw ...`

현재:

- 그냥 회원 목록 반환

왜 가능한가:

- 관리자 여부는 이미 `SecurityConfig`가 `/api/v1/adm/**`에서 막아주기 때문

즉, 컨트롤러가 더 단순해진 이유는
"기능이 줄어서"가 아니라
"권한 판단 책임이 Security로 이동해서"다.

### `ApiV1AdmPostController`

여기도 마찬가지다.

- 예전엔 직접 권한 체크
- 지금은 count만 반환

핵심:

- 권한 정책은 SecurityConfig
- 컨트롤러는 비즈니스 응답

---

## 5. 테스트는 무엇을 보여주나

이번 범위 테스트는 단순 기능 테스트보다
"보안 정책이 어디서 적용되는지"를 보여주는 데 의미가 크다.

### `ApiV1AdmMemberControllerTest`

보여주는 것:

- 관리자면 접근 가능
- 일반 유저면 403

이제 이 403은 컨트롤러 if문보다 Security 정책에 더 가깝다.

### `ApiV1AdmPostControllerTest`

보여주는 것:

- 관리자만 count 조회 가능
- 일반 사용자는 차단

### `ApiV1PostControllerTest`

보여주는 것:

- 공개 API와 보호 API가 어떻게 나뉘는지
- Security 설정 변화에 따라 기존 테스트가 어떻게 바뀌는지

즉, 테스트는 이번 범위에서
"보안 로직의 위치가 이동했다"는 사실을 증명하는 도구다.

---

## 6. 이번 범위가 어려운 이유

이번 범위는 단순히 문법이나 기능 추가가 아니라,
"보안 책임의 위치"가 바뀌는 구간이다.

헷갈리기 쉬운 이유:

1. 컨트롤러 코드가 줄어들었는데 기능은 더 강해짐
2. 인증은 필터에서 처리
3. 권한은 SecurityConfig에서 처리
4. 사용자 정보는 SecurityContext에 저장
5. 실패 응답도 Security가 일부 직접 처리

즉, 코드가 흩어져 보이지만 사실은 역할이 더 분리된 것이다.

---

## 7. 쉽게 기억하는 방법

### 먼저 역할 3개만 기억

- `CustomAuthenticationFilter`: 로그인한 사용자 찾기
- `SecurityConfig`: 누가 어디 들어갈 수 있는지 결정
- `SecurityUser`: Security가 쓰는 사용자 객체

### 그 다음 권한 규칙 기억

- 공개 API: `permitAll`
- 관리자 API: `hasRole("ADMIN")`
- 나머지 API: `authenticated`

### 마지막으로 컨트롤러 기억

- 관리자 컨트롤러는 직접 권한 체크 안 해도 됨
- Security가 먼저 막아줌

---

## 8. 읽는 추천 순서

1. `src/main/java/com/back/global/security/SecurityConfig.java`
2. `src/main/java/com/back/global/security/CustomAuthenticationFilter.java`
3. `src/main/java/com/back/global/security/SecurityUser.java`
4. `src/main/java/com/back/global/security/CustomUserDetailService.java`
5. `src/main/java/com/back/domain/member/entity/Member.java`
6. `src/main/java/com/back/domain/member/controller/ApiV1AdmMemberController.java`
7. `src/main/java/com/back/domain/post/controller/ApiV1AdmPostController.java`
8. `src/test/java/com/back/domain/member/controller/ApiV1AdmMemberControllerTest.java`
9. `src/test/java/com/back/domain/post/controller/ApiV1AdmPostControllerTest.java`
10. `src/test/java/com/back/domain/post/controller/ApiV1PostControllerTest.java`

이 순서가 좋은 이유:

- 먼저 보안 정책
- 다음 인증 객체
- 그 다음 컨트롤러 단순화
- 마지막에 테스트로 확인

---

## 9. 스스로 설명해보면 좋은 질문

1. 왜 관리자 권한 체크를 컨트롤러에서 SecurityConfig로 옮기는 게 더 좋을까?
2. `hasRole("ADMIN")`는 내부적으로 어떤 권한 문자열을 기대할까?
3. `SecurityContextHolder`는 왜 필요한가?
4. 필터 단계 예외는 왜 `@ControllerAdvice` 대신 필터 안에서 직접 응답을 쓸 수 있을까?
5. `SecurityUser`를 따로 만드는 이유는 무엇일까?
6. `authenticated()`와 `hasRole("ADMIN")`는 어떻게 다를까?

---

## 10. 이번 범위를 한 문장으로 정리하면

이번 범위는 "커스텀 인증 필터로 현재 사용자를 `SecurityContext`에 넣고, 관리자 권한 판단을 컨트롤러의 if문에서 Spring Security의 `hasRole` 규칙으로 옮기면서 보안 로직을 더 프레임워크 친화적으로 정리한 단계"였다.
