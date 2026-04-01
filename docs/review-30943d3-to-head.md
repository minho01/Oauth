# Review Note: `30943d3` to `HEAD`

대상 범위:

- 시작 커밋: `30943d3b648cd15c8d7144682cd99948228f67de`
- 마지막 커밋: 현재 `HEAD`

포함 커밋:

1. `ca37b5d` `57`
2. `7a76d77` `58`
3. `708c814` `59`
4. `51e13a5` `60`
5. `6e9da9a` `61-1`
6. `cca3646` `61-2`
7. `67fdc6d` `61-2`
8. `ff0aa3b` `66-1`
9. `fdee4e7` `67`
10. `9af31e7` `68`

이번 범위는 단순 기능 추가보다 "JWT를 실제 인증 흐름에 어떻게 끼워 넣을지", 그리고 "패키지/도메인 구조를 어떻게 더 읽기 좋게 만들지"를 배우는 과정이었다.

---

## 1. 이번 범위의 큰 흐름

이번 범위를 한 줄로 요약하면 이렇다.

- JWT 유틸을 보강해서 "생성"뿐 아니라 "검증/파싱"도 가능하게 만들고
- `Rq`가 accessToken과 apiKey를 함께 해석할 수 있게 발전했으며
- 패키지 구조를 더 자연스럽게 정리하고
- 회원 로그인 응답과 현재 사용자 판단 흐름을 다듬은 구간

핵심 변화는 아래 5가지다.

1. `Ut.jwt`에 `isValid`, `payloadOrNull` 추가
2. `AuthTokenService`가 JWT 생성 + payload 추출까지 담당
3. `Rq`가 `apiKey`와 `accessToken`을 함께 읽고 현재 사용자(`actor`)를 구하는 구조로 발전
4. 패키지 구조를 `domain.post.post` / `domain.post.comment`에서 더 단순한 구조로 정리
5. `Member`와 `BaseEntity`를 수정해서 JWT payload만으로도 임시 Member 객체를 만들 수 있게 함

---

## 2. 커밋 흐름 복습

## `ca37b5d` `57`

핵심 변화:

- `Ut.jwt`에 JWT 검증/파싱 쪽 기능 추가
- JWT 테스트 간소화 및 재구성

의미:

- 이제 JWT를 "만드는 것"에서 멈추지 않고,
- "이 토큰이 유효한지 확인하고 payload를 읽는 것"까지 다룰 수 있게 됐다.

## `7a76d77` `58`

핵심 변화:

- 패키지 구조 리팩터링
- `domain.post.post`, `domain.post.comment` 같은 중첩 구조를 더 단순하게 정리
- 관련 import와 테스트 패키지도 함께 수정

의미:

- 기능 추가라기보다 "읽기 쉬운 구조"로 바꾼 커밋

배울 점:

- 패키지 구조는 기능만큼 중요하다
- 너무 중첩된 이름은 오히려 읽는 사람을 피곤하게 만든다

## `708c814` `59`

핵심 변화:

- `AuthTokenService`에서 secret/expiration을 설정 파일로 주입받도록 변경
- `application.yaml`에 `custom.jwt.*` 추가

의미:

- 하드코딩된 값을 코드 밖 설정으로 이동

배울 점:

- 보안키/만료시간 같은 값은 코드에 직접 박아두기보다 설정으로 빼는 편이 좋다

## `51e13a5` `60`

핵심 변화:

- 회원 로그인 응답/흐름 보강
- `MemberService`에 토큰 관련 기능 연결
- 회원 테스트 보강

의미:

- JWT 생성이 이제 단순 유틸 수준이 아니라 회원 서비스 흐름과 연결되기 시작했다

## `6e9da9a` `61-1`

핵심 변화:

- `Rq`가 accessToken과 apiKey를 같이 다루기 시작

의미:

- 인증 판단이 "DB에서 apiKey로 조회만" 하던 단계에서
- "JWT payload를 먼저 활용"하는 단계로 발전

## `cca3646` `61-2`

핵심 변화:

- `MemberService` 보강
- `Rq` 로직 보강
- 게시글 테스트 보정

의미:

- JWT payload 파싱 결과를 실제 서비스 흐름과 더 안정적으로 연결

## `67fdc6d` `61-2`

핵심 변화:

- 게시글 테스트의 세부 수정

의미:

- 새로운 인증/파싱 구조에 맞게 테스트를 맞춰가는 미세 조정

## `ff0aa3b` `66-1`

핵심 변화:

- 게시글 테스트에서 JWT 관련 시나리오 추가

의미:

- 이제 "토큰이 유효할 때 어떤 흐름으로 동작해야 하는가"를 테스트에서 확인

## `fdee4e7` `67`

핵심 변화:

- 게시글 컨트롤러 코드 정리
- 테스트 보강

의미:

- 기능 구현 뒤 불필요하거나 어색한 부분을 정리한 단계

## `9af31e7` `68`

핵심 변화:

- `Member`에 JWT payload 기반 생성용 생성자 추가
- `BaseEntity`에 protected setter 추가
- `Rq`에서 payload 기반 `Member` 생성 구조 완성

의미:

- DB에서 매번 조회하지 않고도 JWT payload만으로 현재 사용자를 표현할 수 있는 기반이 생겼다

---

## 3. 이번 범위에서 중요한 개념

## 1. JWT 생성과 JWT 검증은 다르다

이전 범위:

- JWT를 만드는 것 중심

이번 범위:

- JWT가 유효한지 확인
- JWT 안에서 payload 읽기
- 그 payload를 이용해 현재 사용자로 해석

즉:

- 생성(create)
- 검증(validate)
- 해석(parse)

세 단계로 개념이 확장됐다.

## 2. 인증 정보가 2개가 되었다

이번 범위의 `Rq`는 두 가지를 함께 다룬다.

- `apiKey`
- `accessToken`

이게 왜 어렵냐면,
"둘 다 인증 정보처럼 보이는데 역할이 조금 다르기" 때문이다.

쉽게 보면:

- `apiKey`: DB에서 회원을 직접 찾는 키
- `accessToken`: 토큰 안 payload를 읽어 현재 사용자 정보를 빠르게 얻는 수단

## 3. 설정값 분리

`secretKey`, `expiration`을 `application.yaml`에서 주입받게 되었다.

배울 점:

- 코드와 설정을 분리해야 환경별 변경이 쉬워진다
- 보안 관련 값은 하드코딩을 줄이는 게 좋다

## 4. 도메인용 "간이 객체" 만들기

`Member(int id, String name)` 생성자는 DB에서 완전한 회원 엔티티를 읽어오지 않아도,
JWT payload에서 꺼낸 값만으로 "현재 사용자"를 표현할 수 있게 해준다.

이건 실무에서도 자주 나오는 패턴이다.

- 토큰에는 최소 정보만 넣고
- 필요한 경우 그걸 현재 사용자 표현 객체로 바꿔 사용

## 5. 패키지 리팩터링

`domain.post.post`처럼 중복된 구조를 줄여서 더 읽기 쉬운 패키지로 바꾸는 것도 학습 포인트다.

배울 점:

- 좋은 패키지 구조는 코드 이해 속도를 올린다
- 이름이 너무 길거나 중복되면 유지보수가 불편해진다

---

## 4. 코드 상세 설명

## 4-1. `Ut.jwt`는 어떻게 발전했나

파일:

- `src/main/java/com/back/standard/ut/Ut.java`

### 이전 역할

- JWT 문자열 생성

### 이번 범위에서 추가된 역할

- JWT 유효성 검사
- JWT payload 추출

### `toString(...)`

이 메서드는 여전히 JWT 생성 공장 역할이다.

입력:

- secret
- expiration
- body(payload)

출력:

- JWT 문자열

이 메서드의 의미는 그대로:

- "payload + 시간 + secret -> JWT"

### `isValid(String jwt, String secret)`

이 메서드는 "이 JWT가 진짜 쓸 수 있는 토큰인지"를 검사한다.

흐름:

1. `secret`으로 서명 검증용 key 생성
2. `Jwts.parser().verifyWith(secretKey)...parse(jwt)` 실행
3. 예외가 안 나면 `true`
4. 예외가 나면 `false`

핵심:

- JWT 검증은 결국 "서명이 맞는가 / 만료되지 않았는가 / 형식이 맞는가"를 보는 과정

### `payloadOrNull(String jwt, String secret)`

이 메서드는:

- JWT가 유효하면 payload를 Map으로 꺼내고
- 유효하지 않으면 `null`을 반환

즉, 이 메서드는 다음 의미를 가진다.

```text
토큰이 정상이면 안의 정보 꺼내기
토큰이 이상하면 그냥 null
```

이 메서드가 중요한 이유:

- `Rq`가 accessToken을 해석할 때 바로 사용되기 때문

---

## 4-2. `AuthTokenService`는 어떻게 바뀌었나

파일:

- `src/main/java/com/back/domain/member/service/AuthTokenService.java`

### 설정 주입

```java
@Value("${custom.jwt.secretPattern}")
private String secretKey;

@Value("${custom.jwt.expiration}")
private long expireTime;
```

의미:

- 더 이상 코드에 비밀키를 박아두지 않음
- 설정 파일에서 읽어옴

이게 좋은 이유:

- 운영 환경별로 값 바꾸기 쉬움
- 코드 수정 없이 설정 변경 가능

### `genAccessToken(Member member)`

역할:

- 회원 객체를 access token으로 바꿈

payload:

- `id`
- `name`

즉, 로그인 성공 시 필요한 최소 사용자 정보를 토큰에 담는 역할

### `payloadOrNull(String jwt)`

역할:

- accessToken을 해석해서 payload 반환

이 메서드는 `Ut.jwt.payloadOrNull(...)`을 감싼 서비스 계층 메서드다.

왜 감싸는가:

- 컨트롤러나 `Rq`가 유틸 내부 구현을 직접 몰라도 되게 하려고
- secretKey 같은 설정 세부사항을 서비스 안에 숨기려고

---

## 4-3. `MemberService`는 왜 바뀌었나

파일:

- `src/main/java/com/back/domain/member/service/MemberService.java`

추가된 핵심:

- `AuthTokenService` 의존
- `genAccessToken(member)`
- `payloadOrNull(jwt)`
- `findById(id)`

이 의미는 다음과 같다.

- 이제 회원 서비스가 단순 DB 조회/가입만 하는 게 아니라
- "회원 기반 인증 토큰 관련 기능"도 연결하기 시작했다

쉽게 말하면:

- 회원가입/로그인 서비스
- + 토큰 서비스 연결자

역할을 가지게 된 것

---

## 4-4. `ApiV1MemberController`는 무엇이 달라졌나

파일:

- `src/main/java/com/back/domain/member/controller/ApiV1MemberController.java`

### 로그인 응답이 달라짐

이전:

- `apiKey`만 응답

현재:

- `apiKey`
- `accessToken`

둘 다 응답

```java
record MemberLoginResBody(
    String apiKey,
    String accessToken
)
```

즉, 로그인 성공 시 클라이언트는:

- DB 조회용 키(apiKey)
- JWT 기반 토큰(accessToken)

둘 다 받는다.

### 로그인 메서드 흐름

1. username으로 회원 찾기
2. 비밀번호 검사
3. `apiKey` 쿠키 저장
4. `memberService.genAccessToken(actor)` 호출
5. `accessToken` 쿠키 저장
6. 응답 바디에도 둘 다 포함

이게 의미하는 바:

- 서버는 인증 수단을 두 개 모두 내려준다
- 이후 요청에서는 `Rq`가 이 둘을 같이 해석한다

즉, 이번 범위는 "토큰 구조를 실험하는 과도기"에 가깝다.

---

## 4-5. `Rq`가 왜 가장 중요한가

파일:

- `src/main/java/com/back/global/rq/Rq.java`

이번 범위의 핵심은 사실상 이 클래스다.

### 목표

- 현재 요청 사용자를 어떻게든 찾아서 `Member actor`로 돌려주는 것

### 지금 `Rq.getActor()` 흐름

크게 4단계로 보면 쉽다.

#### 1. 입력값 모으기

헤더 또는 쿠키에서:

- `apiKey`
- `accessToken`

을 꺼낸다.

헤더가 있으면 헤더 우선,
없으면 쿠키를 본다.

#### 2. 최소 조건 확인

```java
if (apiKey.isBlank()) {
    throw new ServiceException(...)
}
```

즉, 최소한 apiKey는 있어야 한다고 본다.

#### 3. accessToken이 있으면 먼저 해석 시도

```java
Map<String, Object> payload = memberService.payloadOrNull(accessToken);
```

성공하면 payload에서:

- `id`
- `name`

을 꺼내서

```java
member = new Member(id, name);
```

로 현재 사용자를 만든다.

이게 이번 범위에서 제일 중요한 포인트다.

의미:

- accessToken이 유효하면 DB를 바로 조회하지 않고도
- payload만으로 현재 사용자 표현 객체를 만들 수 있다

#### 4. accessToken으로 못 찾으면 apiKey로 fallback

```java
if (member == null) {
    member = memberService.findByApiKey(apiKey) ...
}
```

즉:

- accessToken 성공 -> 토큰 기반으로 actor 생성
- accessToken 실패 -> apiKey로 DB 조회

이 구조가 중요한 이유:

- JWT가 유효하면 빠르게 현재 사용자 해석 가능
- 그래도 apiKey를 최종 fallback으로 둬서 기존 구조도 유지

이건 실습 단계에서 매우 자연스러운 설계다.

---

## 4-6. `Member`, `BaseEntity`는 왜 수정됐나

파일:

- `src/main/java/com/back/domain/member/entity/Member.java`
- `src/main/java/com/back/global/entity/BaseEntity.java`

### `BaseEntity`

추가:

```java
@Setter(AccessLevel.PROTECTED)
private Integer id;
```

왜 필요한가:

- `Member(int id, String name)` 생성자에서
- JWT payload에서 꺼낸 id를 넣어야 했기 때문

즉, 일반 외부 코드가 마음대로 id를 바꾸게 하진 않으면서,
상속받은 엔티티 내부에서는 세팅할 수 있게 만든 것

### `Member(int id, String name)`

이 생성자는 "DB에서 읽은 완전한 회원"이 아니라
"토큰 payload로부터 만든 간이 사용자 표현"에 가깝다.

하는 일:

1. `this.setId(id);`
2. `this.nickname = name;`

즉, 최소한의 정보만 가진 Member 객체를 만든다.

왜 이렇게 하냐:

- 토큰에는 전체 회원 정보가 아니라 필요한 최소 정보만 들어 있으니까

---

## 4-7. `application.yaml`은 왜 중요하나

파일:

- `src/main/resources/application.yaml`

추가된 설정:

```yaml
custom:
  jwt:
    secretPattern: ...
    expiration: "#{1000L * 60 * 60 * 24 * 365}"
```

의미:

- JWT secret과 만료 시간을 설정으로 분리

이 설정이 중요한 이유:

- `AuthTokenService`가 이 값을 주입받기 때문
- 코드와 설정이 분리되면 유지보수가 쉬워진다

---

## 4-8. 테스트는 이번 범위에서 무엇을 검증하나

### `AuthTokenServiceTest`

지금 테스트는 아래를 본다.

1. 서비스가 빈으로 잘 뜨는지
2. `Ut.jwt.toString(...)`이 실제 JWT 문자열을 만드는지
3. `Ut.jwt.isValid(...)`가 유효성을 검증하는지
4. `payloadOrNull(...)`이 payload를 잘 꺼내는지
5. 실제 회원 기반 accessToken 생성이 되는지

즉, JWT 학습의 핵심 개념을 테스트로 한 번씩 찍어본다.

### `ApiV1PostControllerTest`

특히 눈여겨볼 부분:

- accessToken + apiKey 조합 테스트
- 잘못된 apiKey인데 accessToken은 유효한 경우의 동작

이 테스트가 의미하는 건:

- 현재 인증 로직이 단순하지 않다
- `Rq`가 어느 정보를 우선하는지 학습하는 데 매우 중요하다

---

## 5. 이번 범위가 어려운 이유

이번 범위는 아래 개념이 동시에 들어왔다.

1. JWT 생성
2. JWT 검증
3. JWT payload 파싱
4. 설정 주입
5. 현재 사용자 해석
6. fallback 인증 구조
7. 패키지 리팩터링
8. 테스트 재정렬

즉, 단순 문법 문제가 아니라 "인증 설계"와 "프로젝트 구조 정리"를 동시에 건드리는 구간이라 어렵게 느껴지는 게 당연하다.

---

## 6. 쉽게 기억하는 방법

### 먼저 JWT 3단계를 기억

- 만든다
- 검증한다
- payload를 읽는다

### 그 다음 `Rq`를 기억

- 헤더/쿠키에서 읽음
- accessToken 먼저 시도
- 안 되면 apiKey fallback

### 마지막으로 `Member(id, name)`을 기억

- payload만으로 actor를 만들기 위한 생성자

이 3가지만 잡으면 이번 범위의 핵심은 거의 이해한 셈이다.

---

## 7. 코드 분석

## 좋았던 점

### JWT 유틸이 생성에서 검증까지 확장됐다

이제 JWT를 실제 인증 흐름에 연결할 준비가 됐다.

### `Rq`가 더 유연해졌다

accessToken과 apiKey를 함께 해석하는 구조는 실습용으로 꽤 좋은 중간 단계다.

### 패키지 구조가 정리됐다

코드 읽기가 한결 쉬워진다.

## 생각해볼 점

### `Rq` 안에 인증 정책이 꽤 많이 들어왔다

지금은 학습용으로 괜찮지만, 더 커지면 인증 전략을 별도 클래스로 분리할 수도 있다.

### apiKey와 accessToken의 역할이 동시에 존재해 약간 헷갈릴 수 있다

지금 구조는 과도기적이어서 이해에는 좋지만,
실서비스라면 어느 쪽을 주 인증 수단으로 삼을지 더 명확히 할 필요가 있다.

### `Member(int id, String name)`는 "완전한 회원 엔티티"가 아니라 "토큰 기반 표현 객체"라는 점을 기억해야 한다

이걸 놓치면 왜 DB 조회 없이 Member를 만드는지 헷갈릴 수 있다.

---

## 8. 읽는 추천 순서

1. `src/main/java/com/back/standard/ut/Ut.java`
2. `src/main/java/com/back/domain/member/service/AuthTokenService.java`
3. `src/main/java/com/back/domain/member/service/MemberService.java`
4. `src/main/java/com/back/domain/member/entity/Member.java`
5. `src/main/java/com/back/global/entity/BaseEntity.java`
6. `src/main/java/com/back/global/rq/Rq.java`
7. `src/main/java/com/back/domain/member/controller/ApiV1MemberController.java`
8. `src/main/resources/application.yaml`
9. `src/test/java/com/back/domain/member/service/AuthTokenServiceTest.java`
10. `src/test/java/com/back/domain/post/controller/ApiV1PostControllerTest.java`

이 순서가 좋은 이유:

- 먼저 JWT 자체를 이해
- 그 다음 서비스 연결 이해
- 그 다음 actor 해석 흐름 이해
- 마지막으로 테스트로 검증

---

## 9. 스스로 설명해보면 좋은 질문

1. JWT 생성, JWT 검증, JWT payload 파싱은 각각 어떻게 다른가?
2. 왜 `AuthTokenService`가 `Ut.jwt`를 직접 호출하지 않고 서비스 계층으로 감싸는가?
3. `Rq`는 왜 accessToken을 먼저 보고, 안 되면 apiKey를 fallback으로 쓸까?
4. 왜 `Member(id, name)` 같은 생성자가 필요했을까?
5. `BaseEntity`의 protected setter는 왜 여기서 필요했을까?
6. 패키지 이름을 단순하게 바꾸면 어떤 장점이 있을까?

---

## 10. 이번 범위를 한 문장으로 정리하면

이번 범위는 "JWT를 생성하는 단계에서 나아가 검증과 payload 파싱까지 확장하고, 그 정보를 `Rq`에서 현재 사용자 해석에 활용하도록 연결하면서, 패키지 구조와 회원/토큰 흐름을 더 읽기 좋은 형태로 다듬은 과정"이었다.
