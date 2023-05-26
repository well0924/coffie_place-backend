# Coffies Vol.02

### 기존에 Mybatis로 만들었던 프로젝트를 jpa로 컨버팅한 프로젝트 입니다.

## 포트폴리오 명

Coffies vol.02

### 제작인원
1인

### 제작기간

23.03.21~23.05.09(~ing)

- 기본적인 api 완료(5.12)

- 부가적인 기능(5.13~)

-[x] QueryDsl n+1 문제 해결

-[ ] redis 캐싱 적용(로그인v,공지사항v,회원자동완성v,가게 평점,좋아요,조회수,가게 검색)

-[ ] DockerCompose를 활용해서 다중 컨테이너 사용

-[ ] Jenkins을 활용한 CI/CD구축

-[ ] Aws 배포

-제작기술

#### Backend
- java 17
- spring boot 2.7.2
- spring Data Jpa 2.7.5
- spring security 2.7.6
- redis 2.7.9
- query dsl 5.0.0

#### DB
- mariaDB
- H2DB

#### Frontend
- theymleaf
- jQuery
- bootstrap
- fontawesome

#### ETC
- swagger 3.0.0

#### Depoly
- github Action or jenkins
- Aws
- Aws s3

### Git Convention

- feat : 기능 추가
- test : 테스트 코드 작성
- refactor : 리팩토링
- docs : 코드 주석추가 삭제,문서 변경

### ERD

### Api 기능

### Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Handling Form Submission](https://spring.io/guides/gs/handling-form-submission/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)

### 문제 해결

- Jpa N+1 문제해결하기.
- 
