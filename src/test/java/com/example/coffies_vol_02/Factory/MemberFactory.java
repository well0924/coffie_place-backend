package com.example.coffies_vol_02.Factory;

import com.example.coffies_vol_02.config.constant.Role;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.dto.request.MemberRequest;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;

import java.util.Date;

public class MemberFactory {
    public static Member memberDto(){
        return Member
                .builder()
                .id(1)
                .userId("well4149")
                .password("qwer4149!!")
                .memberName("userName")
                .userEmail("well414965@gmail.com")
                .userPhone("010-9999-9999")
                .userAge("20")
                .userGender("남자")
                .userAddr1("xxxxxx시 xxxx")
                .userAddr2("ㄴㅇㄹㅇㄹㅇ")
                .memberLat(0.00)
                .memberLng(0.00)
                .failedAttempt(0)
                .lockTime(new Date())
                .enabled(true)
                .accountNonLocked(true)
                .role(Role.ROLE_ADMIN)
                .build();
    }

    public static MemberRequest request(){
        return new MemberRequest(
                memberDto().getId(),
                memberDto().getUserId(),
                memberDto().getPassword(),
                memberDto().getMemberName(),
                memberDto().getUserPhone(),
                memberDto().getUserGender(),
                memberDto().getUserAge(),
                memberDto().getUserEmail(),
                memberDto().getUserAddr1(),
                memberDto().getUserAddr2(),
                memberDto().getMemberLat(),
                memberDto().getMemberLng(),
                memberDto().getRole());
    }

    public static MemberResponse response(){
        return new MemberResponse(memberDto());
    }
}
