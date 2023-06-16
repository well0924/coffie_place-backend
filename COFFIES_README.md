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

-[ ] Jenkins/github action을 활용한 CI/CD구축

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
- theymleaf 3.0.15
- jQuery 3.6.0
- bootstrap 4.6.0
- fontawesome 5.15.3

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
<img src="https://github.com/well0924/coffie_placeVol.02/assets/89343159/3e19255d-fc9f-4d66-9c65-818ae8bc7c86">

### Api 기능
| api 명                  | url                                              | type | api 설명 |
|------------------------|--------------------------------------------------|---|------|
| memberCreate           | /api/member/join                                 |POST| 회원가입기능 |
| memberIdCheck          | /api/member/id-check/{user_idx}                  |GET| 회원아이디 중복 여부 |
| memberEmailCheck       | /api/member/email-check/{user_email}             |GET| 회원이메일 중복 여부 |
| findUserId             | 	/api/member/find-id/{user_name}/{user_email}    | 	GET | 	회원 아이디 찾기 |
| passwordUpdate         | 	/api/member/password/{user_idx}                 | 	POST | 	비밀번호 재설정기능 |
| memberDelete           | /api/member/delete                               | DELETE| 회원 탈퇴|
| memberUpdate           | /api/member/update/{user_idx}                    | PUT,PATCH | 회원 수정 |
| findMember             | /api/member/detail/{user_idx}                    | GET | 회원정보 단일 조회 |
| memberList             | /api/member/list                                 |GET| 회원 목록|
| memberSearch           | 	/api/mebmer/list/search	                        |GET	|회원검색기능|
| memberNameAutoComplete | 	/api/member/auto-compete-keyword                |GET|	회원 아이디 자동완성|              
| memberSeleteDelete     | 	/api/member/select-delete	                      |DELETE|	회원선택삭제 |                      
| boardAllList           | 	/api/board/list	                                |GET	|게시글 목록|                                    
| findBoard              | 	/api/board/detail/{board_id}                    |	GET|	게시글 단일 조회|                    
| boardSearch            | 	/api/board/list/search                          |	GET	|게시글 검색|                             
| boardCreate            | 	/api/board/write                                |	POST	|게시글 작성 |                                
| boardUpdate            | 	/api/board/update/{board_id}                    |	PATCH,PUT|	게시글 수정|                 
| boardDelete            | 	/api/board/delete/{board_id}                    |	DELETE|	게시글 삭제|                    
| likePlus               | 	/api/like/plus/{board_id}                       |	POST|	게시판 좋아요 추가 |                    
| likeMinus              | 	/api/like/minus/{board_id}                      |	POST|	게시판 좋아요 감소|                    
| placeLikePlus          | 	/api/like/place/plus/{place_id}	                |POST|	가게 댓글 좋아요 추가|             
| placeLikeMinus         | 	/api/like/place/minus/{place_id}                |	DELETE|	가게 댓글 좋아요 감소 |         
| MyWishList             | 	/api/mypage/list	                               |GET	|위시리스트 목록|                                 
| wishListAdd            | 	/api/mypage/wish-add                            |	POST|	위시리스트 추가 |                           
| wishListDelete         | 	/api/mypage/delete/{favorite_id}/{user_id}      |	DELETE|	위시리스트 삭제 |   
| wishListCheck          | 	/api/mypage/wish-check/{favorite_id}/{user_id}  |	GET|	위시리스트 중복 여부 |
| MyArticle              | 	/api/mypage/my-article/{user_id}	               |GET	|내가 작성한 글 확인하기|            
| MyComment              | 	/api/mypage/my-comment/{user_id}	               |GET	|내가 작성한 댓글 확인 |            
| commentList            | 	/api/comment/list/{board_id}	                   |GET|	댓글 목록|                        
| commentCreate          | 	/api/comment/write/{board_id}	                  |POST|	댓글 작성|                      
| commentDelete          | 	/api/comment/delete/{reply_id}                  |	DELETE                         |	댓글 삭제|                    
| placeCommentList       | 	/api/comment/place/list/{place_id}              |	GET	|가게 댓글 목록|                
| placeCommentCreate     | 	/api/comment/place/write/{place_id}	            |POST	|가게 댓글 작성|              
| placeCommentDelete     | 	/api/comment/place/delete/{place_id}/{reply_id} |	DELETE|	가게 댓글 삭제 |
| noticeList             | 	/api/notice/list	                               |GET	|공지게시판 목록 |                                 
| noticeAllSearch        | 	/api/notice/search	                             |GET|	공지게시판 검색 |                               
| noticeDetail           | 	/api/notice/detail/{notice_id}                  |	GET	|공지게시판 단일 조회 |
| noticeCreate           | 	/api/notice/write                               |	POST|	공지게시판 작성 |
| noticeUpdate           | 	/api/notice/update/{notice_id}	                 |PATCH|공지게시판 수정|
| noticeDelete           | 	/api/notice/delete/{notice_id}	                 |DELETE|공지게시판 삭제|
| FreeFileDownload       | 	/api/file/free/download/{id}	                   |GET|	업로드한 파일을 다운로드|
| NoticeFileDownload     | 	/api/file/notice/download/{id}                  |	GET|	업로드한 파일을 다운로드|
| excelDownload          | 	/api/file/place-download?excelDownload=true     |	GET	|등록한 가게 목록을 엑셀로 다운로드|
| placeCreate            | 	/api/place/register                             |	POST	|가게 등록|
| placeUpdate            | 	/api/place/update/{place_id}	                   |PUT,PATCH|	가게수정|
| placeDelete            | 	/api/place/delete/{place_id}                    |	DELETE|	가게삭제|
| placeList              | 	/api/place/list	                                |GET|	가게목록|
| placeDetail            | 	/api/place/detail/{place_id}                    |	|GET|가게단일조회|
| placeSearch            | 	/api/place/list/search                          |	GET|	가게검색|
| placeAutoSearch        | 	/api/place/list/auto                            |	GET|	가게이름자동완성|
| placeTop5List          | 	/api/place/top5list                             |	GET| 	가게 TOP5목록|

### 문제 해결 및 배웠던 부분

- Jpa N+1 문제해결하기.
- QueryDsl 동적정렬 구현하기.
