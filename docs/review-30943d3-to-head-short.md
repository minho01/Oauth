# Review Note Short: `30943d3` to `HEAD`

## 한 줄 요약

이번 범위는 JWT를 "생성만 하는 단계"에서 "검증하고 payload를 읽어서 현재 사용자 판단에 활용하는 단계"로 확장한 구간이다.

## 꼭 기억할 5개

1. `Ut.jwt`가 JWT 생성뿐 아니라 검증(`isValid`)과 payload 추출(`payloadOrNull`)까지 하게 됨
2. `AuthTokenService`가 JWT 관련 기능을 서비스 계층에서 담당하게 됨
3. `Rq`가 `accessToken`과 `apiKey`를 같이 읽어서 현재 사용자(`actor`)를 구함
4. `Member(id, name)` 생성자와 `BaseEntity` 수정으로 JWT payload만으로도 간이 `Member` 생성 가능
5. 패키지 구조가 더 단순하게 정리되어 코드 읽기가 쉬워짐

## 핵심 흐름

### 1. 로그인

- `ApiV1MemberController.login()`
- 회원 확인
- `apiKey` 생성/사용
- `accessToken` 생성
- 둘 다 응답에 포함

### 2. 현재 사용자 구하기

- `Rq.getActor()`
- 헤더/쿠키에서 `apiKey`, `accessToken` 읽기
- `accessToken`이 유효하면 payload에서 `id`, `name` 추출
- `new Member(id, name)`로 현재 사용자 표현
- 안 되면 `apiKey`로 DB 조회 fallback

### 3. JWT 처리

- `Ut.jwt.toString(...)`: JWT 생성
- `Ut.jwt.isValid(...)`: JWT 유효성 검사
- `Ut.jwt.payloadOrNull(...)`: JWT payload 추출

## 파일별 핵심

### [Ut.java](/C:/workspace/p-29-260327/p-26-260310/src/main/java/com/back/standard/ut/Ut.java)

- JWT 유틸 클래스
- 생성 / 검증 / payload 추출 담당

### [AuthTokenService.java](/C:/workspace/p-29-260327/p-26-260310/src/main/java/com/back/domain/member/service/AuthTokenService.java)

- 회원 정보를 accessToken으로 바꿔줌
- 토큰 payload도 읽어줌

### [Rq.java](/C:/workspace/p-29-260327/p-26-260310/src/main/java/com/back/global/rq/Rq.java)

- 현재 요청 사용자 찾기의 중심
- 이번 범위에서 가장 중요한 클래스

### [Member.java](/C:/workspace/p-29-260327/p-26-260310/src/main/java/com/back/domain/member/entity/Member.java)

- `Member(int id, String name)` 생성자 추가
- JWT payload 기반 사용자 표현용

### [BaseEntity.java](/C:/workspace/p-29-260327/p-26-260310/src/main/java/com/back/global/entity/BaseEntity.java)

- `id`에 protected setter 추가
- payload 기반 `Member` 생성 지원

## 개념 초압축

### JWT 생성

- 사용자 정보를 넣고
- 비밀키로 서명해서
- 토큰 문자열을 만든다

### JWT 검증

- 토큰이 위조되지 않았는지
- 형식이 맞는지
- 만료되지 않았는지 확인한다

### payload

- JWT 안에 들어 있는 데이터
- 여기서는 주로 `id`, `name`

### fallback

- 먼저 accessToken으로 시도
- 안 되면 apiKey로 다시 시도

## 왜 어려웠나

- JWT 생성
- JWT 검증
- payload 파싱
- 현재 사용자 해석
- 패키지 구조 변경

이 한 번에 같이 들어와서 어렵게 느껴진 것

## 시험 직전처럼 외우기

- `Ut.jwt`: 만들고, 검사하고, 꺼낸다
- `AuthTokenService`: 회원 -> accessToken
- `Rq`: token 읽고 actor 만든다
- `Member(id, name)`: payload용 간이 회원 객체
- `apiKey 실패 시 fallback이 아니라, accessToken 실패 시 apiKey fallback`

## 추천 최소 읽기 순서

1. [review-30943d3-to-head-short.md](/C:/workspace/p-29-260327/p-26-260310/docs/review-30943d3-to-head-short.md)
2. [Rq.java](/C:/workspace/p-29-260327/p-26-260310/src/main/java/com/back/global/rq/Rq.java)
3. [Ut.java](/C:/workspace/p-29-260327/p-26-260310/src/main/java/com/back/standard/ut/Ut.java)
4. [AuthTokenService.java](/C:/workspace/p-29-260327/p-26-260310/src/main/java/com/back/domain/member/service/AuthTokenService.java)

## 한 문장 정리

이번 범위는 "JWT를 검증하고 payload를 읽어서 현재 사용자 판단에 연결한 것"이 핵심이다.
