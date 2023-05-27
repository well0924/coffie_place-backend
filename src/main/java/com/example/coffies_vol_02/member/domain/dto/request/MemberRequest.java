package com.example.coffies_vol_02.member.domain.dto.request;

import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

public record MemberRequest(Integer id,
                            @NotBlank(message = "아이디를 입력해주세요.")
                            @Pattern(regexp = "^[a-z0-9]{4,15}$",message = "아이디는 영어소문자와 숫자만 사용하고 4~15자까지 입니다.")
                            String userId,
                            @NotBlank(message = "비밀번호를 입력해주세요.")
                            @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,16}$", message = "비밀번호는 8~16자리수여야 합니다. 영문 대소문자, 숫자, 특수문자를 1개 이상 포함해야 합니다.")
                            String password,
                            @NotBlank(message = "회원이름을 입력해주세요.")
                            String memberName,
                            @NotBlank(message = "전화번호를 입력해주세요.")
                            @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$")
                            String userPhone,
                            @NotBlank(message = "성별을  입력해주세요.")
                            String userGender,
                            @NotBlank(message="나이를 입력해주세요.")
                            String userAge,
                            @Email
                            @NotBlank(message = "이메일을 입력해주세요.")
                            String userEmail,
                            @NotBlank(message = "주소를 입력해주세요.")
                            String userAddr1,
                            String userAddr2,
                            Role role){
    public Member toEntity(Member member){
        return Member
                .builder()
                .id(id)
                .userId(userId)
                .password(password)
                .memberName(memberName)
                .userPhone(userPhone)
                .userEmail(userEmail)
                .userAge(userAge)
                .userGender(userGender)
                .userAddr1(userAddr1)
                .userAddr2(userAddr2)
                .role(Role.ROLE_USER)
                .build();
    }

    public void password(String encode) {
    }
}
