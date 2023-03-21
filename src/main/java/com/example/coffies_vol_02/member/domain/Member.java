package com.example.coffies_vol_02.member.domain;

import com.example.coffies_vol_02.config.BaseTime;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
public class Member extends BaseTime {
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

    @Builder
    public Member(Integer id,String userId,String password,String memberName,String userPhone,String userGender,String userAge,String userEmail,String userAddr1,String userAddr2){
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
    }
}
