package com.example.coffies_vol_02.member.domain.dto.response;

import com.example.coffies_vol_02.member.domain.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
public class MemberResponseDto implements Serializable {
    private Integer id;
    private String userId;
    private String password;
    private String memberName;
    private String userPhone;
    private String userGender;
    private String userAge;
    private String userEmail;
    private String userAddr1;
    private String userAddr2;
    private Role role;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedTime;
    @Builder
    public MemberResponseDto(
            Integer id,String userId,String password,String memberName,String userPhone,
            String userGender,String userAge,String userEmail,String userAddr1,String userAddr2,
            Role role, LocalDateTime createdTime,LocalDateTime updatedTime){
        this.id = id;
        this.userId = userId;
        this.password = password;
        this.memberName = memberName;
        this.userPhone = userPhone;
        this.userGender = userGender;
        this.userAge = userAge;
        this.userEmail = userEmail;
        this.userAddr1 = userAddr1;
        this.userAddr2 = userAddr2;
        this.role = role;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }
}
