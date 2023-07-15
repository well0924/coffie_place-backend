package com.example.coffies_vol_02.config;

import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.Role;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Date;

@Profile("tester")
public class TestCustomUserDetailsService implements UserDetailsService {

    private Member getUser(){
        return Member
                .builder()
                .id(1)
                .userId("well4149")
                .password("$2a$12$XcIiB0doaPMx0AoRv0G0f.enty5bjsZADwrmw7SmgNZuI4yQVmRSu")
                .userEmail("test1234@gamil.com")
                .userGender("남자")
                .userPhone("010-8991-8570")
                .userAge("20")
                .memberName("tester1")
                .role(Role.ROLE_ADMIN)
                .userAddr1("서울특별시 xx구 xxx동")
                .userAddr2("")
                .memberLat(0.00)
                .memberLng(0.00)
                .failedAttempt(0)
                .lockTime(new Date())
                .enabled(true)
                .accountNonLocked(true)
                .build();
    }
    
    /**
     * admin 계정으로 가정하기.
     **/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username.equals("well4149")){
            return new CustomUserDetails(getUser());
        }
        return null;
    }
}
