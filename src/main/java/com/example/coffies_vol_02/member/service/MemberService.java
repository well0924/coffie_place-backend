package com.example.coffies_vol_02.member.service;

import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.config.redis.CacheKey;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.dto.request.MemberRequest;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisTemplate<String,Object> redisTemplate;
    //계정 정지기간
    private static final long LOCK_TIME_DURATION = 60 * 60; // 1 hours

    /**
     * 회원 전체목록
     * @author 양경빈
     * @param pageable 페이징시 필요한 페이징 객체
     * @see MemberRepository#findAll(Pageable pageable) 회원 목록을 확인하는 메서드
     * @return list
     **/
    @Transactional(readOnly = true)
    public Page<MemberResponse> findAll(Pageable pageable){
        Page<Member>list = memberRepository.findAll(pageable);
        return list.map(MemberResponse::new);
    }

    /**
     * 회원 검색
     * @author 양경빈
     * @see MemberRepository#findByAllSearch(SearchType, String, Pageable)  회원을 검색하는 인터페이스
     * @param searchVal 검색에 필요한 검색어
     * @param pageable 페이징에 필요한 객체
     * @return Page<MemberResponse> 검색후 회원목록 검색시 검색결과가 없는경우에는 표시가 없음
     * */
    @Transactional(readOnly = true)
    public Page<MemberResponse>findByAllSearch(SearchType searchType,String searchVal, Pageable pageable){
        Page<MemberResponse>searchResult = memberRepository.findByAllSearch(searchType,searchVal,pageable);

        if(searchResult.isEmpty()){
            throw new CustomExceptionHandler(ERRORCODE.NOT_SEARCH_VALUE);
        }
        return searchResult;
    }

    /**
     * 회원 단일 조회
     * @author 양경빈
     * @param id 회원 엔티티 회원 번호
     * @exception CustomExceptionHandler 회원조회시 회원이 없는 경우 예외가 발생(NOT_FOUND_MEMBER)
     * @return memberResponse recoed class
     **/
    @Transactional(readOnly = true)
    public MemberResponse findByMember(Integer id){
        Member findMemberById = memberRepository.findById(id)
                .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER));

        return new MemberResponse(findMemberById);
    }

    /**
     * 회원가입기능
     * 비밀번호 BcryptEncoder 적용
     * @author 양경빈
     * @param request 회원가입에 필요한 record class
     **/
    @Transactional
    public void memberCreate(MemberRequest request){
        Member member = new Member();
        //비밀번호 암호화
        member.setPassword(bCryptPasswordEncoder.encode(request.password()));
        //회원 가입
        memberRepository.save(request.toEntity(member));
    }

    /**
     * 회원 수정
     * @author 양경빈
     * @param id 회원번호
     * @param memberCreateDto 회원 수정에 필요한 record class
     * @exception CustomExceptionHandler 회원 조회시 회원을 찾을 수 없음(NOT_FOUND_MEMBER)
     **/
    @Transactional
    public void memberUpdate(Integer id,MemberRequest memberCreateDto){
        //회원 조회
        Optional<Member>detail = Optional.ofNullable(memberRepository.findById(id)
                .orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));

        Member member = detail.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER));

        member.updateMember(memberCreateDto);
    }

    /**
     * 회원 삭제
     * @author 양경빈
     * @param id 회원번호
     * @exception CustomExceptionHandler 회원 조회시 회원을 찾을 수 없음(NOT_FOUND_MEMBER)
     **/
    @Transactional
    public void memberDelete(Integer id){
        Optional<Member>detail = Optional.ofNullable(memberRepository.findById(id)
                .orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));

        Member member = detail.orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER));

        memberRepository.deleteById(member.getId());
    }

    /**
     * 회원 아이디 중복처리
     * @author 양경빈
     * @param userId 회원 아이디
     * @return default false 중복시에는 true
     **/
    @Transactional
    public Boolean memberIdCheck(String userId){
        return memberRepository.existsByUserId(userId);
    }

    /**
     * 회원 이메일 중복체크
     * @param userEmail 회원 이메일
     * @return default false 중복시에는 true
     **/
    @Transactional
    public Boolean memberEmailCheck(String userEmail){
        return memberRepository.existsByUserEmail(userEmail);
    }

    /**
     * 회원 아이디 찾기
     * @param memberName 회원 이름
     * @param userEmail 회원 이메일
     * @exception CustomExceptionHandler 회원 조회시 회원을 찾을 수 없음(NOT_FOUND_MEMBER)
     * @return userId 회원 아이디
     **/
    @Transactional(readOnly = true)
    public String findUserId(String memberName, String userEmail){
        Optional<Member> member = Optional.ofNullable(memberRepository
                .findByMemberNameAndUserEmail(memberName, userEmail)
                .orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));

        Member detail = member.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER));

        return detail.getUserId();
    }

    /**
     * 비밀번호 재설정
     * @author 양경빈
     * @param id  회원 번호 회원 번호가 없는 경우에는 예외를 발생(NOT_FOUND_MEMBER)
     * @param dto 비밀번호 재설정에 필요한 record class
     * @exception CustomExceptionHandler 회원 조회시 회원을 찾을 수 없음 NOT_FOUND_MEMBER
     * @return detail.getId() 회원 번호
     **/
    @Transactional
    public Integer updatePassword(Integer id, MemberRequest dto){
        Optional<Member>detail = Optional.ofNullable(memberRepository.findById(id)
                .orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));

        detail.ifPresent(member -> {
            if(dto.password()!= null){
                detail.get().setPassword(bCryptPasswordEncoder.encode(dto.password()));
            }
            memberRepository.save(member);
        });

        return detail.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)).getId();
    }

    /**
     *  회원 이름 자동완성기능
     * @author 양경빈
     * @param userId 회원 아이디
     * @return searchList 회원 검색에 필요한 목록들
     **/
    public List<String> memberAutoSearch(String userId){

        HashOperations<String,String,Object>hashOperations = redisTemplate.opsForHash();

        List<Member>nameList = memberRepository.findAll();

        Map<String,Object> nameDateMap = nameList.stream().collect(Collectors.toMap(Member::getUserId,Member::getId));
        //redis에 저장
        hashOperations.putAll(CacheKey.USERNAME,nameDateMap);

        ScanOptions scanOptions = ScanOptions.scanOptions().match(userId+"*").build();

        Cursor<Map.Entry<String,Object>> cursor= hashOperations.scan(CacheKey.USERNAME, scanOptions);

        List<String> searchList = new ArrayList<>();

        while(cursor.hasNext()){
            Map.Entry<String,Object> entry = cursor.next();
            searchList.add(entry.getKey());
        }

        return searchList;
    }

    /**
     * 회원 선택 삭제
     * @author 양경빈
     * @param ids 어드민 페이지에서 체크된 회원번호
     **/
    @Transactional
    public void selectMemberDelete(List<String>ids){
        for(int i=0;i<ids.size();i++){
            memberRepository.deleteAllByUserId(ids);
        }
    }

    /**
     * 로그인 실패 횟수 증가
     * @author 양경빈
     * @param userId failCount의 값을 설정하기 위해 객체로 설정
     **/
    public void increaseFailAttempts(String userId){
        memberRepository.updateFailedAttempts(userId);
    }

    /**
     * 로그인 실패 횟수 확인
     * @author 양경빈
     * @param userIdx 회원 아이디
     * **/
    public Integer FailAttemptsConfirm(String userIdx){
       return memberRepository.failAttemptsCount(userIdx);
    }

    /**
     * 로그인 실패 횟수 초기화
     * @author 양경빈
     * @param userId 회원 아이디
     **/
    public void resetFailedAttempts(String userId){
        Member member = new Member();
        member.setFailedAttempt(0);
        memberRepository.save(member);
    }

    /**
     * 계정 잠금
     * 비밀번호가 특정 횟수 실패시 회원의 계정을 잠근다.
     **/
    public void lock() {
        Member member = new Member();
        member.setAccountNonLocked(false);
        member.setLockTime(new Date());
        memberRepository.save(member);
    }

    /**
     * 계정 잠금해제 유효기간
     * 계정이 잠금되면 특정기간(24시간)까지 계정을 잠금하는 기능
     * 
     * @author 양경빈
     * @param member 회원객체
     * @return false (default)
     **/
    public boolean unlockWhenTimeExpired(Member member) {
        long lockTimeInMillis = member.getLockTime().getTime();
        long currentTimeInMillis =  System.currentTimeMillis();

        //잠금기간이 다 된 경우
        if(lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
            member.setAccountNonLocked(true);
            member.setLockTime(null);
            member.setFailedAttempt(0);

            memberRepository.save(member);

            return true;
        }
        return false;
    }
}