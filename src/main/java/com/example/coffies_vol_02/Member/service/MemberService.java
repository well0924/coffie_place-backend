package com.example.coffies_vol_02.Member.service;

import com.example.coffies_vol_02.Config.Exception.ERRORCODE;
import com.example.coffies_vol_02.Config.Exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Member.domain.Role;
import com.example.coffies_vol_02.Member.domain.dto.MemberDto;
import com.example.coffies_vol_02.Member.repository.MemberRepository;
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

        if(list.isEmpty()){
            throw new CustomExceptionHandler(ERRORCODE.NOT_MEMBER);
        }

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

    @Transactional(readOnly = true)
    public Page<MemberDto.MemberResponseDto>findByAllSearch(String searchVal,Pageable pageable){
        Page<MemberDto.MemberResponseDto>result = memberRepository.findByAllSearch(searchVal,pageable);
        return result;
    }

    /*
     * 회원 단일 조회
     *
     */
    @Transactional(readOnly = true)
    public MemberDto.MemberResponseDto findMemberById(Integer id){
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
     * @param :
     */
    @Transactional
    public Integer memberSave(MemberDto.MemberCreateDto memberCreateDto){

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
        Optional<Member>detail = Optional
                .ofNullable(
                        memberRepository
                                .findById(id)
                                .orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));
        Member member = null;

        if(detail.isPresent()){
            member = detail.get();
        }
        member.updateMember(memberCreateDto);
        int result = member.getId();

        return result;
    }
    /*
     * 회원 삭제
     *
     */
    @Transactional
    public void memberDelete(Integer id){
        Optional<Member>detail = Optional.ofNullable(memberRepository.findById(id).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));
        Member member = null;
        if(detail.isPresent()){
            member = detail.get();
        }
        memberRepository.deleteById(member.getId());
    }
    /*
     * 회원 아이디 중복처리
     *
     */
    @Transactional
    public Boolean existsByUserId(String userId){
        return memberRepository.existsByUserId(userId);
    }

    /*
     * 회원 이메일 중복처리
     *
     */
    @Transactional
    public Boolean existByUserEmail(String userEmail){
        return memberRepository.existsByUserEmail(userEmail);
    }

    /*
     * 회원 아이디 찾기
     *
     */
    @Transactional(readOnly = true)
    public String findByMembernameAndUseremail(String membername, String userEmail){
        Optional<Member> member = memberRepository.findByMemberNameAndUserEmail(membername, userEmail);
        Member detail = null;

        if(member.isPresent()){
            detail = member.get();
        }
        String userid = detail.getUserId();
        return userid;
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
        int updateResult = detail.get().getId();
        return updateResult;
    }

    /*
    *  회원 이름 자동완성기능
    *
    */
    @Transactional
    public Object autoSearch(String searchVal) throws Exception {
        JSONArray arrayObj = new JSONArray();
        JSONObject jsonObj = null;
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