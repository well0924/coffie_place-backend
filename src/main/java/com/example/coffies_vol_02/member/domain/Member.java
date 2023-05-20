package com.example.coffies_vol_02.member.domain;

import com.example.coffies_vol_02.config.BaseTime;
import com.example.coffies_vol_02.favoritePlace.domain.FavoritePlace;
import com.example.coffies_vol_02.member.domain.dto.MemberDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Proxy(lazy = false)
@NoArgsConstructor
@Table(name = "tbl_user",indexes = {
        @Index(name = "member_index1",columnList = "userId"),
        @Index(name = "member_index2",columnList = "memberName"),
        @Index(name = "member_index3",columnList = "userEmail")
})
public class Member extends BaseTime implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Setter
    private String userId;
    @Setter
    private String password;
    private String memberName;
    private String userPhone;
    private String userGender;
    private String userAge;
    private String userEmail;
    private String userAddr1;
    private String userAddr2;
    @Enumerated(EnumType.STRING)
    private Role role;

    @BatchSize(size = 1000)
    @JsonIgnore
    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<FavoritePlace>wishList = new ArrayList<>();

    @Builder
    public Member(Integer id,String userId,String password,String memberName,String userPhone,String userGender,String userAge,String userEmail,String userAddr1,String userAddr2,Role role){
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
        this.getCreatedTime();
        this.getUpdatedTime();
    }

    //회원 수정(Dirty Checking)
    public void updateMember(MemberDto.MemberCreateDto memberCreateDto){
        this.userId = memberCreateDto.getUserId();
        this.memberName = memberCreateDto.getMemberName();
        this.userAge = memberCreateDto.getUserAge();
        this.userEmail = memberCreateDto.getUserEmail();
        this.userGender =  memberCreateDto.getUserGender();
        this.userPhone = memberCreateDto.getUserPhone();
        this.userAddr1 = memberCreateDto.getUserAddr1();
        this.userAddr2 = memberCreateDto.getUserAddr2();
    }

}
