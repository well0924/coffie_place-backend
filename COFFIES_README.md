# Coffies Vol.02

### 프로젝트 소개

 기존에 Mybatis로 만들었던 프로젝트를 jpa로 컨버팅을 한 프로젝트입니다. 

### 프로젝트 목표

### 제작인원
1인

### 제작기간

23.03.21~23.05.09(~ing)

- 기본적인 api 완료(5.12)

- 테스트 코드 작성(5.12)

- 부가적인 기능 및 코드 리팩토링(5.13~)

### 제작기술

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

- theymleaf 3.0.15
- jQuery 3.6.0
- bootstrap 4.6.0
- fontawesome 5.15.3

#### ETC

-swagger 3.0.0

#### Depoly

- github Action or jenkins
- Cloud type

### Vol.01에서 변경점

- Mybatis가 아닌 Jpa로 컨버팅.
- 깃 커밋시 컨벤션을 사용
- redis를 활용한 캐시 사용
- 성능을 개선하기 위해서 리팩토링을 진행
- 단순 crud만이 아닌 기술의 고도화 및 성능 리팩토링 진행
- 견고한 어플리케이션을 제작하는데 목표 -> 테스트 코드작성 및 대용량 데이터와 분산환경의 서비스에도 처리를 할 수 있게 고려중...
- docker compose를 활용
- github action을 활용해서 ci를 구축

### Git Convention

- feat : 기능 추가
- test : 테스트 코드 작성
- refactor : 리팩토링
- docs : 코드 주석추가 삭제,문서 변경

### ERD
<img src="https://github.com/well0924/coffie_placeVol.02/assets/89343159/3e19255d-fc9f-4d66-9c65-818ae8bc7c86">

### Api 기능(명세서)

### 문제 해결 및 배웠던 부분

- Jpa N+1 문제해결하기.
- QueryDsl 동적정렬 구현하기.
- 네이티브 쿼리 트러블 슈팅
- redis 캐시 적용
- github action으로 ci 구축하기.
- kakao map api에 spring retry적용