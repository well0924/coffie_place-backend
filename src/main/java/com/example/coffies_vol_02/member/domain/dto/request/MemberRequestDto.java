package com.example.coffies_vol_02.member.domain.dto.request;

import com.example.coffies_vol_02.member.domain.Role;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto implements Serializable {
    private Integer id;
    @NotBlank(message = "아이디를 입력해주세요.")
    @Pattern(regexp = "^[a-z0-9]{4,15}$",message = "아이디는 영어소문자와 숫자만 사용하고 4~15자까지 입니다.")
    private String userId;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,16}$", message = "비밀번호는 8~16자리수여야 합니다. 영문 대소문자, 숫자, 특수문자를 1개 이상 포함해야 합니다.")
    private String password;
    @NotBlank(message = "회원이름을 입력해주세요.")
    private String memberName;
    @NotBlank(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$")
    private String userPhone;
    @NotBlank(message = "성별을  입력해주세요.")
    private String userGender;
    @NotBlank(message="나이를 입력해주세요.")
    private String userAge;
    @Email
    @NotBlank(message = "이메일을 입력해주세요.")
    private String userEmail;
    @NotBlank(message = "주소를 입력해주세요.")
    private String userAddr1;
    private String userAddr2;
    private Role role;
}
