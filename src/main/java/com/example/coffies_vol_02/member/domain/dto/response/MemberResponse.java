package com.example.coffies_vol_02.member.domain.dto.response;

import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.Role;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record MemberResponse(Integer id,
                             String userId,
                             String password,
                             String memberName,
                             String userPhone,
                             String userGender,
                             String userAge,
                             String userEmail,
                             String userAddr1,
                             String userAddr2,
                             Role role,
                             @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
                             LocalDateTime createdTime) {
    public MemberResponse(Member member){
        this(member.getId(),
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
            member.getCreatedTime());
    }

}
