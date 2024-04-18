package com.example.coffies_vol_02.member.domain;

import com.example.coffies_vol_02.config.BaseTime;
import com.example.coffies_vol_02.config.constant.MemberStatus;
import com.example.coffies_vol_02.config.constant.Role;
import com.example.coffies_vol_02.favoritePlace.domain.FavoritePlace;
import com.example.coffies_vol_02.member.domain.dto.request.MemberRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Entity
@Getter
@NoArgsConstructor
@Table(name = "tbl_user",indexes = {
        @Index(name = "member_index1",columnList = "userId",unique = true),
        @Index(name = "member_index2",columnList = "userAge"),
        @Index(name = "member_index3",columnList = "userEmail",unique = true)
})
@AttributeOverrides({@AttributeOverride(name = "id",column = @Column(name = "id"))})
public class Member extends BaseTime implements Serializable {

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

    @Setter
    private boolean enabled;

    @Setter
    @Column(name = "account_non_locked")
    private Boolean accountNonLocked;

    @Setter
    @Column(name = "failed_attempt")
    private Integer failedAttempt;

    @Setter
    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    //회원 위경도(경도)
    private Double memberLng;

    //회원 위경도(위도)
    private Double memberLat;

    //회원  권한
    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;

    @BatchSize(size = 1000)
    @JsonIgnore
    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<FavoritePlace>wishList = new ArrayList<>();

    @Builder
    public Member(Integer id,
                  String userId,
                  String password,
                  String memberName,
                  String userPhone,
                  String userGender,
                  String userAge,
                  String userEmail,
                  String userAddr1,
                  String userAddr2,
                  Boolean enabled,
                  Boolean accountNonLocked,
                  Integer failedAttempt,
                  LocalDateTime lockTime,
                  Double memberLng,
                  Double memberLat,
                  Role role,
                  MemberStatus memberStatus){
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
        this.enabled = enabled;
        this.accountNonLocked = accountNonLocked;
        this.failedAttempt = failedAttempt;
        this.lockTime = lockTime;
        this.memberLng = memberLng;
        this.memberLat = memberLat;
        this.role = role;
        this.memberStatus = memberStatus;
        this.getCreatedTime();
        this.getUpdatedTime();
    }

    //회원 수정(Dirty Checking)
    public void updateMember(MemberRequest memberCreateDto){
        this.userId = memberCreateDto.userId();
        this.memberName = memberCreateDto.memberName();
        this.userAge = memberCreateDto.userAge();
        this.userEmail = memberCreateDto.userEmail();
        this.userGender =  memberCreateDto.userGender();
        this.userPhone = memberCreateDto.userPhone();
        this.userAddr1 = memberCreateDto.userAddr1();
        this.userAddr2 = memberCreateDto.userAddr2();
        this.memberLat = memberCreateDto.memberLat();
        this.memberLng = memberCreateDto.memberLng();
    }

}
