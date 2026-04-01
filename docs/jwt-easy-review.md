# JWT 쉬운 복기 노트

이 문서는 JWT 부분만 아주 쉽게 다시 보기 위한 축약본이다.

---

## 1. JWT가 뭐냐

JWT는 문자열처럼 생긴 "토큰"이다.

로그인한 사용자를 기억하거나,
사용자 정보를 담아서 클라이언트와 서버가 주고받을 때 쓴다.

겉보기에는 그냥 긴 문자열이지만,
안에는 아래 같은 정보가 들어갈 수 있다.

- 사용자 id
- 사용자 이름
- 발급 시간
- 만료 시간

그리고 마지막에는 "이 토큰이 중간에 바뀌지 않았다"는 걸 확인하기 위한 서명도 들어간다.

---

## 2. 이번 코드에서 JWT는 어디에 있나

관련 파일:

- [AuthTokenService.java](/C:/workspace/p-29-260327/p-26-260310/src/main/java/com/back/domain/member/service/AuthTokenService.java)
- [Ut.java](/C:/workspace/p-29-260327/p-26-260310/src/main/java/com/back/standard/ut/Ut.java)
- [AuthTokenServiceTest.java](/C:/workspace/p-29-260327/p-26-260310/src/test/java/com/back/domain/post/member/service/AuthTokenServiceTest.java)

이번 범위에서 한 일은 크게 3단계다.

1. JWT 라이브러리 붙이기
2. JWT 문자열 만드는 유틸 만들기
3. 회원 정보를 넣어서 access token 만들기

즉, 아직 "JWT로 로그인 인증을 전부 바꾼 상태"는 아니고,
"JWT를 생성하는 법을 실습한 상태"라고 보면 된다.

---

## 3. 가장 쉬운 그림

입력:

- 비밀키
- 만료시간
- 사용자 정보

출력:

- JWT 문자열 1개

예를 들면 이런 느낌이다.

```text
회원 객체 -> 필요한 정보 꺼냄 -> JWT 문자열 생성
```

---

## 4. `AuthTokenService`는 무슨 역할인가

파일:

- [AuthTokenService.java](/C:/workspace/p-29-260327/p-26-260310/src/main/java/com/back/domain/member/service/AuthTokenService.java)

이 클래스는 "회원 객체를 받아서 JWT access token으로 바꿔주는 서비스"다.

핵심 메서드:

```java
public String genAccessToken(Member member)
```

이 메서드가 하는 일:

1. `member`를 받는다
2. 그 안에서 필요한 정보만 고른다
   - `id`
   - `name`
3. `Ut.jwt.toString(...)`에 넘긴다
4. JWT 문자열을 돌려받는다
5. 그 문자열을 최종 결과로 반환한다

즉, 이 메서드는 이렇게 이해하면 된다.

```text
Member -> JWT 문자열
```

---

## 5. `Ut.jwt.toString(...)`은 무슨 역할인가

파일:

- [Ut.java](/C:/workspace/p-29-260327/p-26-260310/src/main/java/com/back/standard/ut/Ut.java)

이 메서드는 "JWT를 실제로 만드는 공장" 같은 역할이다.

서비스에서 바로 JJWT 라이브러리 코드를 길게 쓰지 않으려고
중간에 유틸로 한 번 감싼 것이다.

메서드 모양:

```java
public static String toString(String secret, long expireSeconds, Map<String, Object> body)
```

입력값 의미:

- `secret`
  - 서명용 비밀 문자열
- `expireSeconds`
  - 만료 시간 관련 값
- `body`
  - 토큰 안에 넣고 싶은 정보

출력:

- JWT 문자열

---

## 6. 이 메서드 안에서 실제로 무슨 일이 일어나나

아주 쉽게 줄이면 4단계다.

### 1. payload 준비

```java
Map.of("id", member.getId(), "name", member.getName())
```

이런 식으로 토큰 안에 넣고 싶은 정보를 준비한다.

쉽게 말하면:

- "이 토큰 안에 어떤 데이터를 넣을까?"

를 정하는 단계다.

### 2. 시간 준비

```java
Date issuedAt = new Date();
Date expiration = new Date(...);
```

의미:

- 언제 만들었는지
- 언제 만료되는지

를 넣는다.

### 3. 비밀키 준비

```java
Key secretKey = Keys.hmacShaKeyFor(secret.getBytes());
```

의미:

- 그냥 문자열 비밀키를
- JWT 서명에 쓸 수 있는 Key 객체로 바꾸는 과정

### 4. JWT 만들기

```java
String jwt = Jwts.builder()
        .claims(claims)
        .issuedAt(issuedAt)
        .expiration(expiration)
        .signWith(secretKey)
        .compact();
```

이 부분이 진짜 핵심이다.

뜻을 풀면:

- payload 넣고
- 발급 시간 넣고
- 만료 시간 넣고
- 비밀키로 서명하고
- 최종 문자열로 압축한다

즉:

```text
정보 + 시간 + 비밀키 -> JWT 문자열
```

---

## 7. 테스트는 왜 3단계로 나눴나

파일:

- [AuthTokenServiceTest.java](/C:/workspace/p-29-260327/p-26-260310/src/test/java/com/back/domain/post/member/service/AuthTokenServiceTest.java)

이 테스트는 JWT를 갑자기 한 번에 이해시키지 않고,
쉬운 단계부터 차근차근 올려간다.

### 테스트 1: 라이브러리 직접 써보기

의미:

- "JWT가 진짜 이렇게 만들어지는구나"를 생으로 연습

### 테스트 2: 유틸 메서드로 감싸서 써보기

의미:

- 복잡한 코드를 재사용 가능한 한 줄로 줄이는 연습

### 테스트 3: 실제 회원으로 토큰 만들기

의미:

- 장난감 예제가 아니라
- 실제 프로젝트 도메인 객체를 기반으로 토큰 생성

즉 테스트 흐름은:

```text
라이브러리 이해 -> 유틸 이해 -> 서비스 이해
```

---

## 8. 이번 프로젝트에서 JWT는 지금 어디까지 왔나

중요하다.

현재 상태:

- JWT를 "생성하는 기능"은 있다
- JWT를 "실제 인증에 사용하는 흐름"은 아직 본격 적용 전이다

즉 지금 프로젝트의 실제 인증 중심은 아직 `apiKey`와 `Rq.getActor()` 쪽이다.

JWT 파트는:

- 앞으로 인증을 JWT 기반으로 바꿀 수 있도록
- 먼저 생성 원리를 학습한 단계

라고 이해하면 된다.

---

## 9. 아주 짧게 외우는 버전

### `AuthTokenService`

- 회원을 받아서 JWT 문자열을 만든다

### `Ut.jwt.toString`

- JWT를 실제로 만들어주는 공장 메서드다

### JWT 만들 때 필요한 것

- payload
- 발급 시간
- 만료 시간
- 비밀키

### 이번 테스트의 핵심

- 직접 만들기
- 유틸로 만들기
- 서비스로 만들기

---

## 10. 헷갈리기 쉬운 포인트

### 1. JWT 생성과 JWT 인증은 다르다

지금은 주로 "생성"을 공부한 것이다.

### 2. JWT가 있다고 바로 로그인 끝이 아니다

그 토큰을 이후 요청에서 받고,
검증하고,
현재 사용자로 바꾸는 흐름이 추가로 있어야 한다.

### 3. `apiKey`와 JWT는 같은 게 아니다

- `apiKey`: 서버 DB에 저장된 키를 매번 조회
- JWT: 토큰 자체 안에 정보가 들어 있고 서명으로 검증

---

## 11. 한 문장 요약

이번 JWT 파트는 "회원 정보를 넣어서 서명된 토큰 문자열을 만드는 방법을, 라이브러리 직접 사용 -> 유틸 -> 서비스 순서로 연습한 것"이다.
