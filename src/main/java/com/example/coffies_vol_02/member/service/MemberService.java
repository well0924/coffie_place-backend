package com.example.coffies_vol_02.member.service;

import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.constant.MemberStatus;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.dto.request.MemberRequest;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Log4j2
@Service
@Transactional
@AllArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
     * @return memberResponse record class
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
    public Boolean memberIdCheck(String userId){
        return memberRepository.existsByUserId(userId);
    }

    /**
     * 회원 이메일 중복체크
     * @param userEmail 회원 이메일
     * @return default false 중복시에는 true
     **/
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
     * 회원 선택 삭제
     * @author 양경빈
     * @param ids 어드민 페이지에서 체크된 회원번호
     **/
    public void selectMemberDelete(List<String>ids){
        for(int i=0;i<ids.size();i++){
            memberRepository.deleteAllByUserId(ids);
        }
    }

    /**
     * 회원 계정 잠금
     * 로그인 실패를 3회 실패시 회원계정을 잠금.
     * @param userId : 회원 아이디
     * @exception UsernameNotFoundException : 회원이 없는 경우의 예외처리.
     **/
    public void loginFailed(String userId) {
        Member user = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setFailedAttempt(user.getFailedAttempt() + 1);

        if (user.getFailedAttempt() >= 3) {
            user.setAccountNonLocked(true);
            user.setLockTime(LocalDateTime.now());
            user.setMemberStatus(MemberStatus.USER_LOCK);
        }
        memberRepository.save(user);
    }

    /**
     * 로그인 실패후 계정잠금
     * @author 양경빈
     * @param userId 회원 아이디
     * @exception UsernameNotFoundException : 회원을 찾을 수 없습니다.
     **/
    public boolean isAccountLocked(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        //계정이 잠겨있는 경우
        if (member.getAccountNonLocked() != null && member.getAccountNonLocked()) {
            if(member.getLockTime()!=null){
                long hoursSinceLock = ChronoUnit.HOURS.between(member.getLockTime(), LocalDateTime.now());
                if (hoursSinceLock >= 24) {
                    member.setAccountNonLocked(false);
                    member.setFailedAttempt(0);
                    member.setMemberStatus(MemberStatus.USER_LOCK);
                    memberRepository.save(member);
                    return false;
                }
                return true;
            }else{
                // Lock time이 null인 경우 처리 (필요에 따라 메시지 추가)
                throw new IllegalStateException("Lock time is not set");
            }
        }
        return false;
    }

    /**
     * 로그인 실패횟수 리셋
     * @author : 양경빈
     * @param userId:회원아이디
     * @exception UsernameNotFoundException : 회원을 찾을 수 없습니다.
     **/
    public void resetLoginAttempts(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        member.setFailedAttempt(0);
        member.setAccountNonLocked(true);
        member.setLockTime(null);
        member.setMemberStatus(MemberStatus.NON_USER_LOCK);
        memberRepository.save(member);
    }
    
    /**
     * 계정이 잠금된 이후 24시간 이후에 잠금해제 기능
     **/
    @Scheduled(cron = "0 0 0 * * *")
    public void unlockAccounts(){
        //계정이 잠금된 회원을 찾기.
        List<Member>memberList = memberRepository.existsAllByAccountLocked(LocalDateTime.now().minusHours(24));
        
        log.info(memberList);
        //잠금된 계정이 있는 경우
        //반복문을 사용해서 실패횟수와 계정
        for (Member user : memberList) {
            user.setAccountNonLocked(false);
            user.setFailedAttempt(0);
            memberRepository.save(user);
        }

        if(memberList.isEmpty()){
           log.info("잠겨있는 계정이 없습니다.");
        }
    }
}