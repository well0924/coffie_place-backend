package com.example.coffies_vol_02.member.service;

import com.example.coffies_vol_02.config.exception.ERRORCODE;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisTemplate<String,Object> redisTemplate;
    /**
    * 회원 목록
    *
    **/
    @Transactional(readOnly = true)
    public Page<MemberResponse> findAll(Pageable pageable){
        Page<Member>list = memberRepository.findAll(pageable);
        return list.map(member->new MemberResponse(member));
    }
    
    /**
    *  회원 검색
    * */
    @Transactional(readOnly = true)
    public Page<MemberResponse>findByAllSearch(String searchVal, Pageable pageable){
        return memberRepository.findByAllSearch(searchVal,pageable);
    }

    /*
     * 회원 단일 조회
     *
     */
    @Transactional(readOnly = true)
    public MemberResponse findMemberRecord(Integer id){
        Member findMemberById = memberRepository.findById(id).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_MEMBER));
        MemberResponse memberResponse = new MemberResponse(findMemberById);
        return memberResponse;
    }
    /*
     * 회원가입기능
     */
    @Transactional
    public void memberCreate(MemberRequest request){
        Member member = new Member();
        member.setPassword(bCryptPasswordEncoder.encode(request.password()));

        memberRepository.save(request.toEntity(member));
    }

    /*
     * 회원 수정
     *
     */
    @Transactional
    public void memberUpdate(Integer id,MemberRequest memberCreateDto){
        //회원 조회
        Optional<Member>detail = Optional.ofNullable(memberRepository.findById(id).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));

        Member member = detail.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_MEMBER));

        member.updateMember(memberCreateDto);
    }

    /*
     * 회원 삭제
     *
     */
    @Transactional
    public void memberDelete(Integer id){
        Optional<Member>detail = Optional.ofNullable(memberRepository.findById(id).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));

        Member member = detail.orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER));

        memberRepository.deleteById(member.getId());
    }

    /*
     * 회원 아이디 중복처리
     *
     */
    @Transactional
    public Boolean memberIdCheck(String userId){
        return memberRepository.existsByUserId(userId);
    }

    /*
     * 회원 이메일 중복처리
     *
     */
    @Transactional
    public Boolean memberEmailCheck(String userEmail){
        return memberRepository.existsByUserEmail(userEmail);
    }

    /*
     * 회원 아이디 찾기
     *
     */
    @Transactional(readOnly = true)
    public String findUserId(String membername, String userEmail){
        Optional<Member> member = Optional.ofNullable(memberRepository.findByMemberNameAndUserEmail(membername, userEmail).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_MEMBER)));

        Member detail = member.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_MEMBER));

        return detail.getUserId();
    }

    /*
     * 비밀번호 재설정
     *
     */
    @Transactional
    public Integer updatePassword(Integer id, MemberRequest dto){
        Optional<Member>detail = Optional.ofNullable(memberRepository.findById(id).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));

        detail.ifPresent(member -> {
            if(dto.password()!= null){
                detail.get().setPassword(bCryptPasswordEncoder.encode(dto.password()));
            }
            memberRepository.save(member);
        });

        return detail.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_MEMBER)).getId();
    }

    /**
    *  회원 이름 자동완성기능
    *
    */
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
    *  회원 선택 삭제
    */
    @Transactional
    public void selectMemberDelete(List<String>ids){
        for(int i=0;i<ids.size();i++){
            memberRepository.deleteAllByUserId(ids);
        }
    }
}