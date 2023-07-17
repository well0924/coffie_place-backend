package com.example.coffies_vol_02.member.domain.dto.response;

import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.config.constant.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@ApiModel(value = "회원 응답 dto",description = "회원 응답 dto")
public record MemberResponse(
                             @Schema(description = "회원 번호",type = "Integer")
                             Integer id,
                             @Schema(description = "회원아이디",type = "String")
                             String userId,
                             @Schema(description = "비밓번호",type = "String")
                             String password,
                             @Schema(description = "회원이름",type = "String")
                             String memberName,
                             @Schema(description = "회원 전화번호",type = "String")
                             String userPhone,
                             @Schema(description = "회원 성별",type = "String")
                             String userGender,
                             @Schema(description = "회원 나이",type = "String")
                             String userAge,
                             @Schema(description = "회원 이메일",type = "String")
                             String userEmail,
                             @Schema(description = "회원 주소1",type = "String")
                             String userAddr1,
                             @Schema(description = "회원 주소2",type = "String")
                             String userAddr2,
                             @Schema(description = "회원 경도",type = "Double")
                             Double memberLng,
                             @Schema(description = "회원 위도",type = "Double")
                             Double memberLat,
                             @Schema(description = "회원 등급",example = "ROLE_ADMIN")
                             Role role,
                             @Schema(description = "회원가입일")
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
            member.getMemberLng(),
            member.getMemberLat(),
            member.getRole(),
            member.getCreatedTime());
    }

}
