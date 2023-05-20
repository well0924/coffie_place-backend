package com.example.coffies_vol_02.member.service;

import com.example.coffies_vol_02.config.exception.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.Role;
import com.example.coffies_vol_02.member.domain.dto.MemberDto;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /*
    * 회원 목록
    *
    */
    @Transactional(readOnly = true)
    public Page<MemberDto.MemberResponseDto> findAll(Pageable pageable){
        Page<Member>list = memberRepository.findAll(pageable);

        return list.map(member->new MemberDto.MemberResponseDto(
                member.getId(),
                member.getUserId(),
                member.getPassword(),
                member.getMemberName(),
                member.getUserPhone(),
                member.getUserGender(),
                member.getUserAge(),
                member.getUserEmail(),
                member.getUserAddr1(),
                member.getUserAddr2(),
                member.getRole(),
                member.getCreatedTime(),
                member.getUpdatedTime()));
    }
    
    /**
    *  회원 검색
    * */
    @Transactional(readOnly = true)
    public Page<MemberDto.MemberResponseDto>findByAllSearch(String searchVal,Pageable pageable){
        return memberRepository.findByAllSearch(searchVal,pageable);
    }


    /*
     * 회원 단일 조회
     *
     */
    @Transactional(readOnly = true)
    public MemberDto.MemberResponseDto findMember(Integer id){

        Member findMemberById = memberRepository.findById(id).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_MEMBER));

        return MemberDto.MemberResponseDto
                .builder()
                .id(findMemberById.getId())
                .memberName(findMemberById.getMemberName())
                .userId(findMemberById.getUserId())
                .password(findMemberById.getPassword())
                .userPhone(findMemberById.getUserPhone())
                .userEmail(findMemberById.getUserEmail())
                .userAge(findMemberById.getUserAge())
                .userGender(findMemberById.getUserGender())
                .userAddr1(findMemberById.getUserAddr1())
                .userAddr2(findMemberById.getUserAddr2())
                .role(findMemberById.getRole())
                .createdTime(findMemberById.getCreatedTime())
                .updatedTime(findMemberById.getUpdatedTime())
                .build();

    }

    /*
     * 회원가입기능
     */
    @Transactional
    public Integer memberCreate(MemberDto.MemberCreateDto memberCreateDto){

        Member member = Member
                .builder()
                .id(memberCreateDto.getId())
                .userId(memberCreateDto.getUserId())
                .password(bCryptPasswordEncoder.encode(memberCreateDto.getPassword()))
                .memberName(memberCreateDto.getMemberName())
                .userPhone(memberCreateDto.getUserPhone())
                .userGender(memberCreateDto.getUserGender())
                .userAge(memberCreateDto.getUserAge())
                .userEmail(memberCreateDto.getUserEmail())
                .userAddr1(memberCreateDto.getUserAddr1())
                .userAddr2(memberCreateDto.getUserAddr2())
                .role(Role.ROLE_USER)
                .build();

        memberRepository.save(member);

        return member.getId();
    }

    /*
     * 회원 수정
     *
     */
    @Transactional
    public Integer memberUpdate(Integer id,MemberDto.MemberCreateDto memberCreateDto){
        //회원 조회
        Optional<Member>detail = Optional.ofNullable(memberRepository.findById(id).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));

        Member member = detail.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_MEMBER));

        member.updateMember(memberCreateDto);

        return member.getId();
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
    public Integer updatePassword(Integer id, MemberDto.MemberCreateDto dto){
        Optional<Member>detail = Optional.ofNullable(memberRepository.findById(id).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));

        detail.ifPresent(member -> {
            if(dto.getPassword()!=null){
                detail.get().setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
            }
            memberRepository.save(member);
        });

        return detail.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_MEMBER)).getId();
    }

    /*
    *  회원 이름 자동완성기능
    *
    */
    @Transactional
    public Object autoSearch(String searchVal) throws Exception {
        //jquery ui가 아닌 다른 방법으로 변경예상....
        JSONArray arrayObj = new JSONArray();
        JSONObject jsonObj;
        ArrayList<String> resultlist = new ArrayList<>();

        List<Member>list = memberRepository.findByUserIdStartsWith(searchVal, Sort.by(Sort.Direction.DESC, "userId"));

        for (Member member:list){
            String str = member.getUserId();
            resultlist.add(str);
        }

        for(String str : resultlist){
            jsonObj = new JSONObject();
            jsonObj.put("data",str);
            arrayObj.put(jsonObj);
        }
        return arrayObj;
    }

    /*
    *  회원 선택 삭제
    */
    @Transactional
    public void selectMemberDelete(List<String>ids){
        for(int i=0;i<ids.size();i++){
            memberRepository.deleteAllByUserId(ids);
        }
    }
}