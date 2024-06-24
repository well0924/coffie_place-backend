# Coffies Vol.02

## 1.프로젝트 소개

 기존에 myBatis로 만들었던 CoffiesVol.01 프로젝트를 jpa로 컨버팅을 한 프로젝트입니다. 

### 프로젝트 목표

- Mybatis에서 JPA로 컨버팅하기
- JUnit5,Mockito를 사용해서 테스트 코드를 작성
- Redis를 다양한 방식으로 활용해보기
- scale-out을 고려해서 대용량 트래픽을 고려한 서버 구조 설계
- 성능 테스트 및 모니터링으로 프로젝트의 신뢰성을 높이기
- github Actions를 사용해서 CI/CD구축하기
- 깃 커밋 컨벤션에 따른 커밋 메시지 편의고려하기

### 제작인원

1인

### 제작기간

23.03.21~ing

### vol.2.0 에서의 변경점

- 기본적인 api 완료(5.12)


- 테스트 코드 작성(5.12~ing)


- 부가적인 기능 및 코드 리팩토링(5.13~)
    
    - **Mybatis가 아닌 JPA로 변경.**
    - **Redis를 활용한 캐시 사용(공지글/가게조회/좋아요)**
    - **깃 커밋시 컨벤션을 사용**
    - **회원 위치에서 가까운 가게 5곳 마이페이지에서 확인**
    - **어드민 페이지 최근에 작성한 글,댓글**
    - **이메일 인증기능 비동기처리**
    - **회원 아이디 및 가게이름 자동완성 (Jquery -> Redis) 수정**
    - **회원 계정 정지기능**
    - **외부 api호출시 실패 방지를 위해서 @Retry를 적용 -> 가게 목록이 아닌 가까운 가게 보여주기에 적용할 예정**
    - **가게 api 호출후 크롤링으로 가게정보 가져오는 방식 -> CSV 파일로 가게를 저장(api 호출 문제로 api사용을 안하고 크롤링으로 대체)**
    - **redis Sorted Set을 활용해서 가게 평점 top5 를 구현**
    - **가게 최근 검색어 기능구현**
    - **Redisson을 사용해서 게시글의 좋아요,조회수 댓글 좋아요에 동시성 제어**
    - **Scale-out을 고려해서 redis Session을 Session Storage로 적용**
    


### 기술 스택
  
#### Backend

- JAVA 17
- Spring boot 2.7.2
- Spring Data JPA 2.7.5
- Spring Security 2.7.6
- Redis 2.7.9
- Query DSL 5.0.0

#### DB

- mariaDB

#### Frontend

- Theymleaf 3.0.15
- Jquery 3.6.0
- Bootstrap 4.6.0
- Fontawesome 5.15.3

#### TEST

- Mockito
- JUnit5

#### DOCS

- Swagger 3.0.0

#### Infra

- Github Actions
- Aws S3
- Aws RDS
- Aws EC2
- Aws Route
- Docker

### Git Convention

- feat : 기능 추가
- test : 테스트 코드 작성 및 수정
- refactor : 리팩토링(기능 개선 및 수정)
- docs : 코드 주석추가 삭제,문서 변경

### 메서드 작성규칙

- 목록:list+도메인명
- 검색:search+도메인명
- 단일조회:find+도메인명/(By)컬럼명
- 작성및 생성:create+도메인명
- 수정기능:update + 도메인명
- 삭제기능:delete+도메인명
- 여부를 따지는 기능: has + 도메인/컬럼명

### 아키텍쳐

![회원 가게 정보 가져오기 drawio](https://github.com/well0924/coffie_placeVol.02/assets/89343159/b80163a1-24b1-46f6-80f8-b5f1ba9ed2d7)

### ERD

![프로젝트 ERD](https://github.com/well0924/coffie_placeVol.02/assets/89343159/085c6641-e14f-45a0-8113-f4f317c5a218)


### Api 명세서

Swagger를 사용한 명세서


### 문제 해결 및 배웠던 부분

- **[Jpa N+1 문제해결하기.](https://codingweb.tistory.com/130)**


- **[@Async를 활용해서 구글 이메일 api 전송속도 개선](https://codingweb.tistory.com/146)**


- **[Redis-Session을 활용해서 Session Storage 구축](https://codingweb.tistory.com/197)**


- **Redisson을 활용한 게시글 좋아요/조회수 댓글 좋아요 동시성 제어**


- **[Redis 캐시와 세션을 분리하기.](https://codingweb.tistory.com/195)**



