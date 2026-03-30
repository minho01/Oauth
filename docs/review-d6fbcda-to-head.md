# Review Note: `d6fbcda` to `HEAD`

대상 범위:

- 시작 커밋: `d6fbcda7451435995e8e3af1d5df1b505588cadc`
- 마지막 커밋: 현재 `HEAD`

포함 커밋:

1. `14fb574` `23`
2. `e2902d7` `24`
3. `8ccf787` `25`
4. `f5046ed` `27`
5. `2b70831` `28,29`
6. `3f7d9db` `30,31`

이 문서는 "무엇이 바뀌었는가"보다 "왜 이런 방향으로 바뀌었는가"와 "현재 코드가 어떤 흐름으로 동작하는가"를 중심으로 복기하기 위한 자료다.

---

## 1. 전체 흐름 한눈에 보기

이번 범위의 핵심 변화는 아래 4가지로 요약할 수 있다.

1. 인증 정보를 `apiKey` 쿼리 파라미터에서 `Authorization: Bearer ...` 헤더로 이동
2. 인증 사용자 조회를 `Rq`라는 요청 전용 객체로 공통화
3. 게시글/댓글의 수정, 삭제 권한 체크 추가
4. 현재 로그인한 회원 정보를 보는 `/api/v1/members/me` API 추가

즉, 이전까지는 "로그인하고 글을 쓸 수 있는 API"였다면,
이번 범위에서는 "인증 사용자 식별이 정리되고, 작성자 권한까지 체크하는 API"로 한 단계 발전했다.

---

## 2. 커밋 흐름 복습

## `14fb574` `23`

핵심 변화:

- `Member`의 `apiKey` 값 사용 방식 조정
- `MemberService`, `BaseInitData` 보강
- `ApiV1PostController`에서 인증 처리 흐름 변경 시작
- 게시글 테스트 보강

이 커밋의 의미:

- 게시글 작성/수정/삭제에서 "누가 요청했는가"를 더 명확히 다루기 시작한 지점이다.

배울 점:

- 인증 정보는 단순 파라미터 전달보다 일관된 규칙이 필요하다.
- 테스트는 새로운 인증 방식에 맞춰 같이 진화해야 한다.

## `e2902d7` `24`

핵심 변화:

- `src/main/java/com/back/global/rq/Rq.java` 추가

이 커밋의 의미:

- 인증 사용자 조회 로직을 컨트롤러마다 반복하지 않고, 요청 단위 객체로 묶는 기반이 생겼다.

배울 점:

- 공통 관심사를 별도 객체로 분리하는 패턴
- `@RequestScope`를 통한 "요청마다 다른 객체" 사용

## `8ccf787` `25`

핵심 변화:

- `ApiV1PostController`가 `Rq`를 사용하도록 리팩터링
- 기존의 인증 파라미터 처리 일부 제거
- 게시글 테스트를 `Authorization` 헤더 방식으로 변경

이 커밋의 의미:

- "인증 처리 위치"가 컨트롤러 내부 분산 구조에서 `Rq` 중심 구조로 정리되었다.

배울 점:

- 컨트롤러는 요청 처리에 집중
- 인증 사용자 식별은 별도 공통 객체에 위임

## `f5046ed` `27`

핵심 변화:

- `Post` 엔티티에 권한 체크 메서드 추가
- 게시글 수정/삭제에서 작성자 권한 검증 추가
- `PostService` 보강

이 커밋의 의미:

- "인증된 사용자"와 "권한이 있는 사용자"는 다르다는 점이 코드에 반영되었다.

배울 점:

- 인증과 권한의 차이
- 도메인 엔티티 내부에서 자기 보호 규칙을 가질 수 있다는 점

## `2b70831` `28,29`

핵심 변화:

- 게시글 관련 테스트 대폭 보강
- 잘못된 Authorization 형식
- 잘못된 API Key
- 작성자가 아닌 사용자의 수정/삭제 시도

이 커밋의 의미:

- 정상 흐름뿐 아니라 실패/권한 오류 시나리오까지 테스트 범위를 넓혔다.

배울 점:

- 좋은 API 테스트는 성공만 보는 것이 아니라 실패 케이스를 같이 검증한다.

## `3f7d9db` `30,31`

핵심 변화:

- `ApiV1MemberController`에 `/me` 추가
- `ApiV1CommentController`가 `Rq` 기반 인증 사용
- `Comment` 엔티티에 권한 체크 메서드 추가
- 댓글 테스트와 회원 `/me` 테스트 추가

이 커밋의 의미:

- 인증 공통화가 게시글에서 댓글, 회원 조회까지 확장되었다.

배울 점:

- 공통 인증 구조가 있으면 다른 컨트롤러로 확장하기 쉬워진다.

---

## 3. 핵심 개념 정리

## 1. Authorization Header

이전 방식:

- `?apiKey=...`

현재 방식:

- `Authorization: Bearer {apiKey}`

의미:

- 인증 정보가 URL에 노출되지 않는다.
- REST API에서 더 일반적인 인증 전달 방식에 가까워진다.

## 2. RequestScope

`Rq`는 `@RequestScope`로 선언되어 있다.

의미:

- HTTP 요청이 들어올 때마다 새 `Rq` 객체가 만들어진다.
- 요청이 끝나면 그 객체도 함께 끝난다.

장점:

- 현재 요청과 관련된 정보를 안전하게 다룰 수 있다.

## 3. 인증(Authentication)과 권한(Authorization)

인증:

- "너 누구냐?"
- `Rq.getActor()`로 현재 요청 사용자를 찾는 것

권한:

- "그 행동을 해도 되냐?"
- `post.checkModify(actor)`
- `comment.checkActorDelete(actor)`

배운 포인트:

- 로그인되어 있다고 해서 모든 글/댓글을 수정할 수 있는 것은 아니다.

## 4. 도메인 엔티티의 자기 보호

`Post`, `Comment`에 권한 체크 메서드가 들어갔다.

의미:

- 엔티티가 "내가 수정/삭제 가능한 상황인지"를 스스로 판단한다.
- 규칙이 컨트롤러에 흩어지지 않는다.

## 5. 실패 케이스 테스트

이번 범위에서 테스트는 아래를 적극적으로 검증한다.

- Authorization 헤더 누락
- Bearer 형식 오류
- 잘못된 apiKey
- 작성자가 아닌 사용자의 수정/삭제

이건 실습에서 아주 중요한 포인트다.
API는 성공 응답만큼 실패 응답 설계도 중요하다.

---

## 4. 코드 흐름 복기

## 4-1. 게시글 작성 흐름

대상 파일:

- `ApiV1PostController`
- `Rq`
- `PostService`

흐름:

1. 클라이언트가 `POST /api/v1/posts` 요청
2. 헤더에 `Authorization: Bearer {apiKey}` 포함
3. 컨트롤러에서 `rq.getActor()` 호출
4. `Rq`가 헤더를 읽는다
5. 헤더가 없으면 `401-1`
6. `Bearer ` 형식이 아니면 `401-2`
7. apiKey로 회원 조회
8. 회원이 없으면 `401-1`
9. 회원이 있으면 `PostService.write(actor, title, content)` 호출
10. 게시글 저장
11. `PostDto`로 응답 생성

여기서 중요한 것은:

- 컨트롤러는 더 이상 apiKey를 직접 파싱하지 않는다.
- 현재 요청 사용자 판단은 `Rq`가 담당한다.

## 4-2. 게시글 수정 흐름

흐름:

1. `PUT /api/v1/posts/{id}`
2. `rq.getActor()`로 현재 사용자 조회
3. `postService.findById(id)`로 대상 글 조회
4. `post.checkModify(actor)` 실행
5. 작성자가 아니면 `403-1`
6. 작성자면 수정 진행
7. 수정 결과를 `RsData`로 반환

핵심:

- 인증 성공 후 바로 수정하는 게 아니라
- "이 글의 작성자인가?"를 한 번 더 확인한다.

## 4-3. 게시글 삭제 흐름

흐름:

1. `DELETE /api/v1/posts/{id}`
2. `rq.getActor()`
3. 글 조회
4. `post.checkDelete(actor)`
5. 작성자가 아니면 `403-2`
6. 맞으면 삭제

핵심:

- 수정/삭제 권한을 분리된 resultCode로 관리한다.

## 4-4. 댓글 작성 흐름

이전에는 댓글 작성자가 `user1`로 고정된 구조였다.
이번 범위 이후에는 아래처럼 바뀌었다.

1. `POST /api/v1/posts/{postId}/comments`
2. `rq.getActor()`로 현재 사용자 조회
3. 게시글 조회
4. `post.addComment(actor, content)`
5. 댓글 저장
6. 댓글 DTO 응답

의미:

- 이제 댓글도 로그인한 사용자 기준으로 작성된다.

## 4-5. 댓글 수정/삭제 흐름

댓글은 `Comment` 엔티티 안에 권한 체크 메서드가 들어갔다.

수정:

1. 현재 사용자 조회
2. 게시글 조회
3. 댓글 조회
4. `comment.checkActorModify(actor)`
5. 작성자가 아니면 `403-1`
6. 맞으면 수정

삭제:

1. 현재 사용자 조회
2. 게시글 조회
3. 댓글 조회
4. `comment.checkActorDelete(actor)`
5. 작성자가 아니면 `403-2`
6. 맞으면 삭제

핵심:

- 게시글은 `Post`가 자기 권한을 체크
- 댓글은 `Comment`가 자기 권한을 체크

즉, 권한 규칙이 해당 도메인 객체 내부에 들어갔다.

## 4-6. 현재 로그인 사용자 조회 `/me`

대상:

- `GET /api/v1/members/me`

흐름:

1. 요청 헤더에서 `Authorization: Bearer ...`
2. `rq.getActor()` 호출
3. 현재 회원 객체 조회
4. `MemberDto`로 반환

의미:

- "이 apiKey가 누구 것인지" 확인하는 API
- 로그인 이후 클라이언트가 자기 정보를 가져오는 기본 API로 볼 수 있다.

---

## 5. 클래스별 역할 변화

## `Rq`

역할:

- 현재 HTTP 요청의 인증 사용자를 찾는 공통 객체

핵심 메서드:

- `getActor()`

하는 일:

- 헤더 읽기
- Bearer 형식 검증
- apiKey 추출
- 회원 조회
- 실패 시 `ServiceException`

이 클래스를 통해 얻는 장점:

- 컨트롤러마다 인증 로직 복붙 방지
- 인증 실패 정책 일관성 유지

## `ApiV1PostController`

이번 범위에서 가장 많이 정리된 컨트롤러다.

이전:

- 요청 파라미터로 apiKey 처리

현재:

- `Rq`를 통한 인증 사용자 조회
- 수정/삭제 시 권한 체크 추가

의미:

- 게시글 컨트롤러가 더 REST스럽고 안전한 구조가 됨

## `Post`

추가된 핵심:

- `checkModify(Member actor)`
- `checkDelete(Member actor)`

의미:

- 게시글이 자기 수정/삭제 권한 규칙을 스스로 가진다.

## `Comment`

추가된 핵심:

- `checkActorModify(Member actor)`
- `checkActorDelete(Member actor)`

의미:

- 댓글도 자기 권한 규칙을 스스로 가진다.

## `ApiV1CommentController`

변화:

- 더 이상 `user1` 하드코딩 작성자 사용 안 함
- `Rq` 기반 현재 사용자 사용
- 수정/삭제 시 댓글 작성자 권한 체크

## `ApiV1MemberController`

변화:

- `/me` 추가

의미:

- 인증 시스템이 실제로 동작하는지 확인할 수 있는 기본 API가 생겼다.

---

## 6. 테스트 관점에서 복기

이번 범위 테스트의 핵심은 "정상 동작 확인"보다 "보안 규칙이 맞게 막히는지"를 보는 데 있다.

### 게시글 테스트에서 추가로 보는 것

- Authorization 헤더가 없을 때 401
- Bearer 형식이 아닐 때 401
- apiKey가 틀릴 때 401
- 작성자가 아닌 사람이 수정하면 403
- 작성자가 아닌 사람이 삭제하면 403

### 댓글 테스트에서 추가로 보는 것

- 로그인한 사용자 기준으로 댓글 작성
- 작성자가 아닌 사람이 수정하면 403
- 작성자가 아닌 사람이 삭제하면 403

### 회원 테스트에서 추가로 보는 것

- `/me` 호출 시 현재 사용자 정보 반환

복기 포인트:

- 테스트는 "기능이 된다"만 확인하는 도구가 아니다.
- "막혀야 할 것이 막히는지"를 증명하는 도구이기도 하다.

---

## 7. 이번 범위에서 배운 핵심 문장

### 1. 인증 정보는 URL보다 헤더가 더 자연스럽다

- `Authorization: Bearer ...`

### 2. 인증 사용자 조회는 공통화할수록 좋다

- `Rq.getActor()`

### 3. 로그인과 권한은 다르다

- 로그인 성공 != 모든 리소스 수정 가능

### 4. 도메인 객체도 자기 규칙을 가질 수 있다

- `Post.checkModify(...)`
- `Comment.checkActorDelete(...)`

### 5. 실패 시나리오 테스트가 API 품질을 높인다

- 401
- 403
- 잘못된 헤더 형식
- 잘못된 apiKey

---

## 8. 코드 분석

## 좋았던 점

### 인증 로직이 공통화됐다

이전보다 컨트롤러가 훨씬 깔끔해졌다.
헤더 파싱과 사용자 조회를 한 곳으로 모은 건 좋은 설계 방향이다.

### 권한 체크가 명확해졌다

수정/삭제에서 작성자 확인 로직이 추가되면서 실제 서비스에 가까운 동작이 됐다.

### 테스트가 기능 변화와 같이 갔다

특히 보안/권한 관련 테스트가 같이 보강된 점이 좋다.

## 아쉬운 점

### `Rq`의 오류 코드 설계는 더 다듬을 수 있다

헤더 누락과 잘못된 apiKey가 같은 `401-1`로 보이는 부분은 상황에 따라 더 세분화할 수도 있다.

### 인코딩 깨짐이 아직 남아 있다

한글 메시지가 일부 깨져 보인다.
문자열 의미를 파악하는 데 방해가 된다.

### `ApiV1PostController`의 포맷/정렬이 약간 흐트러진 부분이 있다

예를 들어 `delete()` 내부 줄 정렬은 한 번 정리하면 더 읽기 좋아진다.

### 권한 체크 메서드 네이밍은 통일 가능하다

- `checkModify`
- `checkDelete`
- `checkActorModify`
- `checkActorDelete`

이런 이름은 나중에 규칙을 통일하면 더 좋다.

---

## 9. 읽는 추천 순서

1. `src/main/java/com/back/global/rq/Rq.java`
2. `src/main/java/com/back/domain/member/controller/ApiV1MemberController.java`
3. `src/main/java/com/back/domain/post/post/controller/ApiV1PostController.java`
4. `src/main/java/com/back/domain/post/post/entity/Post.java`
5. `src/main/java/com/back/domain/post/comment/controller/ApiV1CommentController.java`
6. `src/main/java/com/back/domain/post/comment/entity/Comment.java`
7. `src/test/java/com/back/domain/post/post/controller/ApiV1PostControllerTest.java`
8. `src/test/java/com/back/domain/post/comment/controller/ApiV1CommentControllerTest.java`
9. `src/test/java/com/back/domain/post/member/controller/ApiV1MemberControllerTest.java`

이 순서로 보면:

- 인증 공통화
- 회원 확인
- 게시글 권한
- 댓글 권한
- 테스트 검증

흐름이 자연스럽게 이어진다.

---

## 10. 스스로 설명해보면 좋은 질문

1. 왜 `apiKey`를 쿼리 파라미터보다 Authorization 헤더로 옮겼을까?
2. `Rq`를 만들면 컨트롤러에서 어떤 중복이 사라질까?
3. 인증(Authentication)과 권한(Authorization)은 어떻게 다를까?
4. 왜 권한 체크를 `Post`, `Comment` 엔티티 안에 둘 수 있을까?
5. `/me` API는 클라이언트 입장에서 왜 유용할까?
6. 왜 401 테스트뿐 아니라 403 테스트도 필요한가?

---

## 11. 이번 범위를 한 문장으로 정리하면

이번 범위는 "Bearer 헤더 기반 인증 사용자 조회를 `Rq`로 공통화하고, 게시글/댓글의 작성자 권한 체크와 현재 사용자 조회 API까지 추가해 API를 더 실제 서비스 구조에 가깝게 만든 단계"였다.
