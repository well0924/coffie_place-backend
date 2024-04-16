package com.example.coffies_vol_02.member.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@ApiModel(value = "로그인 요청Dto")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    
    @Schema(description = "회원 아이디")
    private String userId;
    @Schema(description = "회원 비밀번호")
    private String password;
}
