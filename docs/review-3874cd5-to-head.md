# Review Note: `3874cd5` to `HEAD`

대상 범위:

- 시작 커밋: `3874cd5448dd4a8fbb7ce42a7d452a03b7557280` (`3874cd5`, `init`)
- 마지막 커밋: `1887541d706140d314df2d0a710e8ff0d7711cff`

이 문서는 오늘 실습에서 진행한 커밋들을 복습하기 위한 자료다.
단순히 "뭐가 바뀌었는지"만 정리하지 않고, 아래를 함께 묶어서 본다.

- 어떤 개념을 사용했는가
- 코드가 어떤 흐름으로 동작하는가
- 각 패키지와 클래스는 무슨 역할을 하는가
- 현재 코드에서 무엇을 잘했고, 무엇을 더 개선할 수 있는가

---

## 1. 전체 흐름 한눈에 보기

`3874cd5` 이후 현재 프로젝트는 아래 방향으로 확장됐다.

1. 회원(Member) 도메인 추가
2. 기존 게시글/댓글 구조를 API 중심 구조로 정리
3. 게시글/댓글 응답에 작성자 정보 포함
4. API 테스트 강화
5. 게시글 생성 시 인증 개념(apiKey) 도입
6. 서비스 예외(`ServiceException`)와 전역 예외 처리 도입
7. 회원가입 API 및 회원가입 테스트 추가
8. 중복 아이디 검사의 책임을 컨트롤러에서 서비스로 이동

즉, 오늘 실습의 큰 줄기는 다음과 같이 정리할 수 있다.

- "단순 CRUD 코드"에서
- "도메인 관계를 가진 REST API"로
- 그리고 "검증, 예외 처리, 테스트를 갖춘 구조"로 발전하는 과정

---

## 2. 커밋 흐름 복습

### `3874cd5` `init`

실습의 기준점이 되는 초기 커밋이다.
이후 커밋들은 이 상태를 바탕으로 회원 기능과 예외 처리, 테스트를 확장해 간다.

### `e616095` `2,3`

핵심 변화:

- `Member` 엔티티 추가
- `MemberRepository`, `MemberService` 추가
- 게시글과 댓글의 작성자를 `Member`로 연결
- 기존 MVC 성격의 컨트롤러 삭제
- `ApiV1...Controller` 중심 구조로 정리
- 초기 데이터(`BaseInitData`)에 회원/작성자 데이터 연결

이 커밋에서 배운 점:

- 엔티티 간 연관관계
- `Repository`와 `Service` 계층 분리
- "글쓴이"를 문자열이 아니라 실제 엔티티로 모델링하는 방법
- UI 중심 컨트롤러에서 REST API 중심 컨트롤러로 전환하는 흐름

### `fd377ca` `4`

핵심 변화:

- `Member`에 이름 관련 기능 추가
- `PostDto`에 작성자 정보가 담기도록 확장

이 커밋에서 배운 점:

- 엔티티를 직접 노출하지 않고 DTO를 통해 응답 구조를 제어하는 방법
- 게시글 응답에 작성자 메타데이터를 넣는 방식

### `ff7fbc1` `6`

핵심 변화:

- `CommentDto`에도 작성자 정보 추가

이 커밋에서 배운 점:

- 게시글과 댓글 응답이 비슷한 규칙으로 정리되기 시작함
- DTO 설계를 일관되게 가져가는 중요성

### `cc7bffe` `7`

핵심 변화:

- 게시글/댓글 테스트를 작성자 정보 검증까지 포함하도록 보강

이 커밋에서 배운 점:

- 기능 추가 뒤에는 테스트 기대값도 함께 업데이트해야 함
- 응답 스펙이 바뀌면 테스트도 함께 진화해야 함

### `a695ccb` `8`

핵심 변화:

- 게시글 작성 API에 `apiKey` 기반 작성자 식별 로직 추가

이 커밋에서 배운 점:

- 요청 사용자를 식별하는 최소한의 인증 개념
- "누가 글을 작성했는가"를 하드코딩이 아니라 요청값으로 연결하는 방법

### `8107f0d` `9`

핵심 변화:

- 게시글 작성 시 잘못된 `apiKey` 처리 보강

이 커밋에서 배운 점:

- 인증 실패를 단순 `null` 처리 대신 명시적인 예외로 다뤄야 함
- 비정상 흐름도 API 설계의 일부라는 점

### `90f8397` `10`

핵심 변화:

- `ServiceException` 도입
- `GlobalExceptionHandler`에서 서비스 예외 처리 추가

이 커밋에서 배운 점:

- 비즈니스 예외를 공통 타입으로 감싸는 패턴
- 컨트롤러 곳곳에서 `try-catch`하지 않고 전역 예외 처리로 응답 통일

### `039b867` `11~14-2`

핵심 변화:

- 회원가입 API 추가
- `MemberDto` 추가
- `MemberRepository`에 `findByApiKey()` 추가
- 회원가입 테스트 추가
- 개발 프로필 설정 보강

이 커밋에서 배운 점:

- 새로운 도메인 기능을 API, DTO, 서비스, 테스트까지 한 묶음으로 확장하는 방법
- 회원가입 응답에서 엔티티 대신 DTO를 사용하는 이유

### `6c41aeb` `15`

핵심 변화:

- 회원가입 시 중복 아이디 검사 추가

이 커밋에서 배운 점:

- 사용자 입력 검증 중 "중복 검사"는 핵심 비즈니스 규칙이라는 점
- 단순 `@Valid`만으로 해결되지 않는 검증이 있다는 점

### `1887541` `16`

핵심 변화:

- 중복 아이디 검사 로직을 컨트롤러에서 서비스로 이동

이 커밋에서 배운 점:

- 비즈니스 규칙은 서비스에 두는 편이 더 자연스럽다
- 컨트롤러는 요청/응답 조립, 서비스는 핵심 규칙 처리라는 역할 분리가 더 명확해졌다

---

## 3. 현재 프로젝트 구조

### 패키지 구조

- `com.back`
- `com.back.domain.home`
- `com.back.domain.member`
- `com.back.domain.post.post`
- `com.back.domain.post.comment`
- `com.back.global`

### 구조를 큰 그림으로 보면

- `domain`: 실제 비즈니스 기능
- `global`: 전역 공통 기능
- `resources`: 환경 설정
- `test`: API 동작 검증

---

## 4. 패키지와 클래스 역할 정리

## `com.back`

### `RestApiApplication`

- 스프링 부트 실행 진입점
- `@EnableJpaAuditing`으로 생성일/수정일 자동 기록 기능 활성화

핵심 개념:

- Spring Boot 엔트리포인트
- JPA Auditing

## `com.back.domain.home.controller`

### `HomeController`

- 루트 페이지 응답
- Swagger UI 링크 제공
- 간단한 fetch 테스트 HTML 제공

역할:

- API 자체보다는 진입 페이지와 테스트용 보조 역할

## `com.back.domain.member`

### `entity.Member`

- 회원 엔티티
- `username`, `password`, `nickname`, `apiKey` 보유
- `apiKey`는 생성 시 UUID로 생성

역할:

- 작성자와 회원가입 주체를 표현하는 핵심 도메인 객체

핵심 개념:

- JPA 엔티티
- 고유 식별자
- 유니크 컬럼

### `repository.MemberRepository`

- 회원 영속성 처리
- `findByUsername()`
- `findByApiKey()`

역할:

- DB 접근 책임 담당

핵심 개념:

- Spring Data JPA
- 메서드 이름 기반 쿼리 생성

### `service.MemberService`

- 회원 가입 처리
- 회원 수 조회
- username/apiKey로 회원 조회
- 현재는 회원가입 시 중복 username 검사도 담당

역할:

- 회원 관련 비즈니스 규칙 중심 계층

핵심 개념:

- 서비스 계층
- 비즈니스 로직 집중
- 예외 기반 검증

### `dto.MemberDto`

- 회원 응답용 DTO
- `id`, `name`, `createDate`, `modifyDate` 포함

역할:

- 엔티티 직접 노출 방지
- 응답 형태 고정

### `controller.ApiV1MemberController`

- `/api/v1/members`
- 회원가입 요청 수신
- 요청 바디를 record로 받음
- 서비스 호출 후 `RsData<MemberDto>` 반환

역할:

- HTTP 요청을 비즈니스 호출로 연결하는 진입점

핵심 개념:

- REST Controller
- RequestBody
- Validation

## `com.back.domain.post.post`

### `entity.Post`

- 게시글 엔티티
- 제목, 내용, 작성자 보유
- 댓글 목록과 연관관계 보유
- 댓글 추가/조회/수정/삭제 메서드 제공

역할:

- 게시글과 댓글 집합의 중심 엔티티

핵심 개념:

- `@ManyToOne`
- `@OneToMany`
- 도메인 메서드
- 연관관계 편의 메서드 성격

### `repository.PostRepository`

- 게시글 DB 접근 담당

### `service.PostService`

- 게시글 작성, 수정, 삭제, 조회, flush 처리

역할:

- 게시글 비즈니스 로직 담당

### `dto.PostDto`

- 게시글 응답용 DTO
- 작성자 ID/이름 포함

### `controller.ApiV1PostController`

- `/api/v1/posts`
- 게시글 목록 조회
- 게시글 단건 조회
- 게시글 작성
- 게시글 수정
- 게시글 삭제

역할:

- 게시글 API의 핵심 진입점

핵심 개념:

- CRUD API
- 요청 DTO(record)
- 검증 애노테이션
- `apiKey` 기반 요청 사용자 식별

## `com.back.domain.post.comment`

### `entity.Comment`

- 댓글 엔티티
- 내용, 게시글, 작성자 보유

### `dto.CommentDto`

- 댓글 응답용 DTO
- 작성자 정보 포함

### `controller.ApiV1CommentController`

- `/api/v1/posts/{postId}/comments`
- 댓글 목록 조회
- 댓글 단건 조회
- 댓글 작성
- 댓글 수정
- 댓글 삭제

역할:

- 게시글 하위 리소스인 댓글 API 담당

핵심 개념:

- 중첩 리소스 URI
- 게시글과 댓글의 부모-자식 관계 표현

## `com.back.global`

### `entity.BaseEntity`

- 모든 엔티티가 공통으로 상속
- `id`, `createDate`, `modifyDate` 제공

의미:

- 중복 제거
- 엔티티 공통 속성 표준화

### `rsData.RsData`

- 공통 응답 포맷
- `msg`, `resultCode`, `data`
- `resultCode` 앞자리로 HTTP 상태코드 계산

의미:

- 응답 형식 통일

### `aspect.ResponseAspect`

- 컨트롤러 반환값이 `RsData`면 `resultCode`에 맞춰 HTTP 상태코드 설정

의미:

- 응답 바디와 HTTP status를 연결하는 공통 처리

주의할 점:

- 동작은 흥미롭지만, 일반적인 `ResponseEntity` 방식보다 이해 비용이 조금 있다.

### `exception.ServiceException`

- 비즈니스 예외 표현용 런타임 예외
- 내부에서 `RsData`로 변환 가능

### `exceptionHandler.GlobalExceptionHandler`

- `NoSuchElementException`
- `MethodArgumentNotValidException`
- `HttpMessageNotReadableException`
- `ServiceException`
- `HandlerMethodValidationException`

등을 공통 처리

의미:

- 에러 응답 형식을 일관되게 유지

### `initData.BaseInitData`

- 앱 시작 시 샘플 회원/게시글/댓글 생성

의미:

- 테스트와 실습 환경에서 즉시 데이터가 있는 상태를 제공

### `springDoc.SpringDoc`

- Swagger/OpenAPI 그룹 설정

### `webMvc.WebMvcConfig`

- `/api/**` 경로에 대한 CORS 설정

---

## 5. 코드 흐름 복습

## 회원가입 흐름

1. 클라이언트가 `POST /api/v1/members` 요청
2. `ApiV1MemberController.join()`이 요청 바디를 받음
3. `MemberService.join()` 호출
4. 서비스에서 username 중복 검사
5. 중복이면 `ServiceException("409-1", ...)` 발생
6. 아니면 `Member` 생성 후 저장
7. `MemberDto`로 변환
8. `RsData`로 응답
9. `ResponseAspect`가 `201` 상태코드로 세팅

여기서 중요한 포인트:

- 유효성 검사와 비즈니스 검사는 다르다
- `@Valid`는 형식 검증
- 중복 username은 서비스 규칙 검증

## 게시글 작성 흐름

1. 클라이언트가 `POST /api/v1/posts?apiKey=...`
2. `ApiV1PostController.write()` 진입
3. `MemberService.findByApiKey()`로 작성자 조회
4. 없으면 `ServiceException("401-1", ...)`
5. 있으면 `PostService.write()` 호출
6. `Post` 저장
7. `PostDto` 생성
8. `RsData` 응답

핵심 포인트:

- 글 작성자는 요청 파라미터에서 식별
- 작성자 정보는 `PostDto`로 내려감

## 댓글 작성 흐름

1. 클라이언트가 `POST /api/v1/posts/{postId}/comments`
2. `ApiV1CommentController.write()` 진입
3. 대상 게시글 조회
4. 게시글 엔티티의 `addComment()` 호출
5. JPA 연관관계를 통해 댓글 반영
6. `CommentDto` 생성
7. `RsData` 응답

핵심 포인트:

- 댓글은 게시글에 속한 하위 리소스
- 현재 댓글 작성자는 하드코딩되어 있어 향후 개선 여지가 있다

---

## 6. 오늘 실습에서 사용된 핵심 개념

### 1. Layered Architecture

- Controller
- Service
- Repository
- Entity
- DTO

배운 점:

- 각 계층이 무엇을 책임져야 하는지

### 2. JPA Entity Mapping

- `@Entity`
- `@ManyToOne`
- `@OneToMany`
- `cascade`
- `orphanRemoval`

배운 점:

- 객체 관계를 DB 관계로 표현하는 방법

### 3. DTO 변환

- 엔티티를 직접 응답하지 않음
- 필요한 필드만 DTO로 전달

배운 점:

- API 스펙 안정성
- 민감한 필드 숨기기

### 4. Validation

- `@Valid`
- `@NotBlank`
- `@Size`

배운 점:

- 입력 형식 검증을 선언적으로 처리할 수 있음

### 5. Exception Handling

- 서비스 예외 분리
- 전역 예외 처리

배운 점:

- 에러 응답을 통일하면 API 사용성과 유지보수성이 좋아짐

### 6. Auditing

- `@CreatedDate`
- `@LastModifiedDate`
- `@EnableJpaAuditing`

배운 점:

- 공통 메타데이터 자동 관리

### 7. API Testing

- `MockMvc`
- 상태코드 검증
- JSON 경로 검증

배운 점:

- 컨트롤러 기능이 아니라 "API 계약"을 검증하는 감각

### 8. Seed Data

- 시작 시 샘플 데이터 주입

배운 점:

- 실습 속도를 높이고 테스트를 단순하게 만들 수 있음

---

## 7. 코드 분석 포인트

## 좋았던 점

### 서비스 계층으로 비즈니스 규칙 이동

최신 커밋에서 중복 username 검사를 서비스로 이동한 방향은 좋다.
이제 `join()`이 어디서 호출되든 동일한 규칙을 적용할 수 있다.

### DTO 사용이 일관적이다

게시글, 댓글, 회원 모두 DTO를 사용하고 있어 응답 구조가 깔끔하다.

### 테스트가 함께 추가됐다

회원가입 기능처럼 새로운 기능이 생길 때 테스트도 같이 늘어난 점이 좋다.

### 공통 처리에 대한 감각이 생겼다

- `BaseEntity`
- `RsData`
- `GlobalExceptionHandler`

이런 공통 장치들을 둔 것은 구조적으로 의미가 크다.

## 개선해볼 점

### `ServiceException` 버그 가능성

현재 `ServiceException` 생성자에서 `resultCode` 필드 대입이 빠져 있다.
그래서 `getRsData()` 호출 시 `resultCode`가 `null`일 가능성이 있다.

복습 포인트:

- 예외 객체를 만들 때 필요한 상태가 모두 저장되는지 확인하기

### 댓글 작성자의 하드코딩

댓글 작성 API는 아직 `user1`을 고정으로 사용한다.
게시글 작성 API처럼 인증 정보를 받아 작성자를 결정하도록 발전시킬 수 있다.

### `ResponseAspect` 방식의 복잡성

`RsData.resultCode`에서 HTTP status를 꺼내 응답에 반영하는 아이디어는 재밌다.
다만 처음 읽는 사람에게는 흐름이 조금 숨겨져 있다.

복습 포인트:

- 공통화는 좋지만, 가독성과 명시성의 균형도 중요하다

### 동시성 관점의 회원가입 검증

서비스에서 중복 username을 미리 확인하는 건 좋다.
하지만 동시에 두 요청이 들어오면 DB 유니크 제약도 함께 믿어야 안전하다.

복습 포인트:

- 서비스 검증과 DB 무결성은 함께 가는 경우가 많다

### 인코딩 문제

현재 일부 한글 문자열이 깨져 보인다.
실습 환경에서 파일 인코딩을 통일하는 습관이 중요하다.

---

## 8. 공부할 때 다시 보면 좋은 파일 순서

1. `src/main/java/com/back/RestApiApplication.java`
2. `src/main/java/com/back/global/entity/BaseEntity.java`
3. `src/main/java/com/back/domain/member/entity/Member.java`
4. `src/main/java/com/back/domain/post/post/entity/Post.java`
5. `src/main/java/com/back/domain/post/comment/entity/Comment.java`
6. `src/main/java/com/back/domain/member/service/MemberService.java`
7. `src/main/java/com/back/domain/post/post/service/PostService.java`
8. `src/main/java/com/back/domain/member/controller/ApiV1MemberController.java`
9. `src/main/java/com/back/domain/post/post/controller/ApiV1PostController.java`
10. `src/main/java/com/back/domain/post/comment/controller/ApiV1CommentController.java`
11. `src/main/java/com/back/global/exceptionHandler/GlobalExceptionHandler.java`
12. `src/test/java/com/back/domain/post/post/controller/ApiV1PostControllerTest.java`
13. `src/test/java/com/back/domain/post/comment/controller/ApiV1CommentControllerTest.java`
14. `src/test/java/com/back/domain/post/member/controller/ApiV1MemberControllerTest.java`

이 순서로 보면:

- 엔티티 구조
- 비즈니스 로직
- API 진입점
- 예외 처리
- 테스트

순으로 자연스럽게 이해할 수 있다.

---

## 9. 스스로 설명해보면 좋은 질문

1. 왜 엔티티를 바로 응답하지 않고 DTO로 감쌌을까?
2. `Controller`와 `Service`의 책임은 어떻게 다를까?
3. `Member`를 따로 엔티티로 분리했을 때 어떤 이점이 생겼을까?
4. `apiKey`로 작성자를 찾는 구조는 어떤 장점과 한계가 있을까?
5. `ServiceException`과 `GlobalExceptionHandler`를 같이 쓰는 이유는 무엇일까?
6. 게시글과 댓글의 관계를 왜 `Post` 중심으로 다루고 있을까?
7. 중복 username 검사를 서비스에 두는 것이 왜 더 좋은가?
8. 테스트는 단순히 실행 확인이 아니라 무엇을 보장하는가?

---

## 10. 이번 실습을 한 문장으로 정리하면

이번 실습은 "Spring Boot 기반 REST API에서 게시글/댓글/회원 도메인을 연결하고, DTO, 검증, 예외 처리, 테스트까지 갖춘 구조로 확장하는 과정"이었다.

---

## 11. 다음에 이어서 공부하면 좋은 주제

- `ResponseEntity`와 현재 `RsData + Aspect` 방식 비교
- Spring Security 없이 간단 인증을 구현할 때의 한계
- JPA 연관관계 편의 메서드 정리
- DB 유니크 제약 예외를 잡아 사용자 친화 메시지로 바꾸는 방법
- 통합 테스트와 단위 테스트의 차이
- 요청/응답 스펙 문서화와 Swagger 활용
