package com.example.coffies_vol_02.member.service;

import com.example.coffies_vol_02.config.Exception.ERRORCODE;
import com.example.coffies_vol_02.config.Exception.RestApiException;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.dto.MemberDto;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    /*
    * 회원 목록
    *
    */
    @Transactional(readOnly = true)
    public Page<MemberDto.MemberResponseDto> findAll(Pageable pageable){
        Page<Member>list = memberRepository.findAll(pageable);
        //회원이 없는 경우
        if(list.isEmpty()){
            throw new RestApiException(ERRORCODE.NOT_FOUND_MEMBER);
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
    /*
     * 회원 단일 조회
     *
     */
    @Transactional(readOnly = true)
    public MemberDto.MemberResponseDto findMemberById(Integer id){
        Member findMemberById = memberRepository.findById(id).orElseThrow(()->new RestApiException(ERRORCODE.NOT_FOUND_MEMBER));

        return MemberDto.MemberResponseDto
                .builder()
                .id(findMemberById.getId())
                .memberName(findMemberById.getMemberName())
                .userId(findMemberById.getUserId())
                .password(findMemberById.getPassword())
                .userPhone(findMemberById.getUserAge())
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
     *
     */
    @Transactional
    public Integer memberSave(MemberDto.MemberCreateDto memberCreateDto){
        Member member = memberCreateDto.toEntity();
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
        Optional<Member>detail = Optional.ofNullable(memberRepository.findById(id).orElseThrow(() -> new RestApiException(ERRORCODE.NOT_FOUND_MEMBER)));
        Member member = detail.get();
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
        Optional<Member>detail = Optional.ofNullable(memberRepository.findById(id).orElseThrow(() -> new RestApiException(ERRORCODE.NOT_FOUND_MEMBER)));
        Member member = detail.get();
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
    public String findByMemberNameAndUserEmail(String membername, String userEmail){
        Optional<Member> member = memberRepository.findByMemberNameAndUserEmail(membername, userEmail);
        Member detail = member.get();
        return detail.getUserId();
    }

    /*
     * 비밀번호 재설정
     *
     */
    @Transactional
    public Integer updatePassword(Integer id, MemberDto.MemberCreateDto dto){
        Optional<Member>detail = Optional.ofNullable(memberRepository.findById(id).orElseThrow(() -> new RestApiException(ERRORCODE.NOT_FOUND_MEMBER)));

        detail.ifPresent(member -> {
            if(dto.getPassword()!=null){
                detail.get().setPassword(dto.getPassword());
            }
            memberRepository.save(member);
        });

        return detail.get().getId();
    }


}