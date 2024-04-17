# Coffies Vol.02

## 1.프로젝트 소개

 기존에 Mybatis로 만들었던 CoffiesVol.01 프로젝트를 jpa로 컨버팅을 한 프로젝트입니다. 

### 프로젝트 목표

- **Mybatis에서 Jpa로 컨버팅**
- **Mockito와 Junit을 사용해서 테스트 코드작성**
- **redis를 사용해서 캐싱처리 하기**
- **QueryDsl을 사용하고 성능향상(인덱스 적용)**
- nginx + githubaction을 사용해서 무중단 배포 + 로드벨런싱 구현
- Docket-compose를 사용해 보기.
- Jmeter를 사용해서 api의 성능 테스트
- Prometheus를 사용해서 로그 수집

### 제작인원

1인

### 제작기간

23.03.21~ing

### vol.2.0 에서의 변경점

- 기본적인 api 완료(5.12)


- 테스트 코드 작성(5.12)


- 부가적인 기능 및 코드 리팩토링(5.13~)
    
    - **Mybatis가 아닌 Jpa로 변경.**
    - **redis를 활용한 캐시 사용**
    - **깃 커밋시 컨벤션을 사용**
    - **회원 위치에서 가까운 가게 5곳 마이페이지에서 확인**
    - **어드민 페이지 최근에 작성한 글,댓글**
    - **이메일 인증기능 비동기처리**
    - **회원 아이디 및 가게이름 자동완성 (Jquery -> redis) 수정**
    - **회원 계정 정지기능**
    - **외부 api호출시 실패 방지를 위해서 @Retry를 적용**
    - **가게 api 호출후 크롤링으로 가게정보 가져오는 방식 -> CSV 파일로 가게를 저장**
    - redis SortedSet을 활용해서 가게 평점 top5 를 구현
    - redisson을 사용해서 게시글의 좋아요,조회수 댓글 좋아요에 동시성 제어
    - **Scale-out을 고려해서 redis Session-Clustering 적용**

### 기술 스택
  
#### Backend

- java 17
- spring boot 2.7.2
- spring Data Jpa 2.7.5
- spring security 2.7.6
- redis 2.7.9
- query dsl 5.0.0

#### DB

- mariaDB

#### Frontend

- theymleaf 3.0.15 -> React로 변경하기.
- jQuery 3.6.0 -> x
- bootstrap 4.6.0
- fontawesome 5.15.3

#### TEST

- Mockito
- JUnit5

#### DOCS

-swagger 3.0.0 -> 포스트맨으로 할지

#### Infra

- github Action
- Aws S3
- Aws RDBS
- Aws EC2
- Aws Route

### Git Convention

- feat : 기능 추가
- test : 테스트 코드 작성 및 수정
- refactor : 리팩토링(기능 개선 및 수정)
- docs : 코드 주석추가 삭제,문서 변경

### 아키텍쳐

![CoffiesVol.02아키텍처.drawio.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/8905e7b5-bab8-45f3-8ca5-d4462e06684e/7521748d-e0af-4631-8eb0-308e78142fba/CoffiesVol.02%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98.drawio.png)

### ERD

![coffies Vol.02.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/0c33679e-3dbf-44d0-b3d5-a4db2035d954/coffies_Vol.02.png)

### Api 명세서

### Git 컨벤션

feat : 새로운 기능을 추가하는 경우

refactor : 프로덕션 코드 리팩토링

test : 테스트 추가,테스트 리팩토링

docs : 주석을 추가,수정 및 스웨거 코드 변경

### 문제 해결 및 배웠던 부분

- **Jpa N+1 문제해결하기.**


- **redis 캐시 적용**


- **redis-session을 활용해서 세션 클러스터링 구축**


- **@Async로 외부api 성능개선 하기.**

