package com.example.coffies_vol_02.member.domain.dto.request;

import com.example.coffies_vol_02.config.constant.MemberStatus;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.config.constant.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@ApiModel(value = "회원요청Dto")
public record MemberRequest(
                            @Schema(description = "회원번호")
                            Integer id,
                            @Schema(description = "회원아이디",pattern = "^[a-z0-9]{4,15}$")
                            @NotBlank(message = "아이디를 입력해주세요.")
                            @Pattern(regexp = "^[a-z0-9]{4,15}$",message = "아이디는 영어소문자와 숫자만 사용하고 4~15자까지 입니다.")
                            String userId,
                            @Schema(description = "비밀번호",pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,16}$")
                            @NotBlank(message = "비밀번호를 입력해주세요.")
                            @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,16}$", message = "비밀번호는 8~16자리수여야 합니다. 영문 대소문자, 숫자, 특수문자를 1개 이상 포함해야 합니다.")
                            String password,
                            @Schema(description = "회원이름")
                            @NotBlank(message = "회원이름을 입력해주세요.")
                            String memberName,
                            @Schema(description = "회원 전화번호",pattern = "^\\d{2,3}-\\d{3,4}-\\d{4}$")
                            @NotBlank(message = "전화번호를 입력해주세요.")
                            @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$",message = "전화번호를 입력해 주세요.")
                            String userPhone,
                            @Schema(description = "회원 성별")
                            @NotBlank(message = "성별을  입력해주세요.")
                            String userGender,
                            @Schema(description = "회원 나이")
                            @NotBlank(message="나이를 입력해주세요.")
                            String userAge,
                            @Schema(description = "회원 이메일",example = "example1234@naver.com")
                            @Email
                            @NotBlank(message = "이메일을 입력해주세요.")
                            String userEmail,
                            @Schema(description = "회원 주소1")
                            @NotBlank(message = "주소를 입력해주세요.")
                            String userAddr1,
                            @Schema(description = "회원 주소2")
                            String userAddr2,
                            @Schema(description = "회원 위도")
                            Double memberLat,
                            @Schema(description = "회원 경도")
                            Double memberLng,
                            @Schema(description = "회원 등급",example = "ROLE_ADMIN",allowableValues = {"ROLE_ADMIN","ROLE_USER"})
                            Role role){

    public Member toEntity(Member member){
        return Member
                .builder()
                .id(id)
                .userId(userId)
                .password(member.getPassword())
                .memberName(memberName)
                .userPhone(userPhone)
                .userEmail(userEmail)
                .userAge(userAge)
                .userGender(userGender)
                .userAddr1(userAddr1)
                .userAddr2(userAddr2)
                .role(Role.ROLE_USER)
                .memberLng(memberLng)
                .memberLat(memberLat)
                .failedAttempt(0)
                .enabled(true)
                .accountNonLocked(true)
                .memberStatus(MemberStatus.NON_USER_LOCK)
                .build();
    }

    public void password(String encode) {
    }
}
