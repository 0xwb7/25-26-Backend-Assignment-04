# 25-26-Backend-Assignment-04

- 한 명의 user가 여러 개의 post, comment를 작성할 수 있는 1:N 관계 구현
- 한 post에 여러 comment를 달 수 있는 1:N 관계 구현

## Auth

- 회원가입: `/auth/signup`
    - 기본적으로 `ROLE_USER`로 가입이 됨
    - 201 Created, 응답 바디 x
    - 잘못된 입력이 들어가거나 중복된 이메일 입력 시 예외 메시지 출력

- 로그인: `/auth/login`
    - 로그인 성공 시 `accessToken`, `refreshToken` 발급
    - 잘못된 이메일이나 비밀번호 입력 시, 400 + 예외 메시지 출력

- refreshToken을 통한 Token 재발급: `/auth/refresh`
    - Body로 `refreshToken` 값을 전송
    - 유효한 토큰일 경우 새로운 `accessToken` , `refreshToken`  발급
    - DB에 유저 당 1개의 `refreshToken`  보유 가능
    - 유효한 토큰이 아닐 경우 예외메시지 출력

## Post

- 생성: `POST /posts`
    - 글 작성을 위해선 토큰 인증 필요
    - 본인의 사용자 정보로 author 설정됨

- 조회: `GET /posts/{id}`
    - 누구나 접근 가능토록 함
    - 글 + 댓글 목록이 함께 출력

- 수정: `PATCH /posts/{id}`
    - 작성자 본인 또는 `ROLE_ADMIN`만 수정 가능

- 삭제: `DELETE /posts/{id}`
    - 작성자 본인 또는 `ROLE_ADMIN`만 수정 가능
    - 삭제한 글에 달린 댓글도 함께 삭제됨

## Comment

- 생성: `POST /posts/{postId}/comments`
    - 댓글 작성을 위해선 토큰 인증 필요
    - 작성 시, 게시글 내 댓글 추가됨

- 수정: `PATCH /comments/{id}`
    - 작성자 본인 또는 `ROLE_ADMIN`만 수정 가능

- 삭제: `DELETE /comments/{id}`
    - 작성자 본인 또는 `ROLE_ADMIN`만 수정 가능