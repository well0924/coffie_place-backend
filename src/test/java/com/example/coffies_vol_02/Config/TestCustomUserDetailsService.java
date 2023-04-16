package com.example.coffies_vol_02.Config;

import com.example.coffies_vol_02.Config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Member.domain.Role;
import com.example.coffies_vol_02.Member.repository.MemberRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Profile("tester")
public class TestCustomUserDetailsService implements UserDetailsService {
    private MemberRepository repository;
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
                .build();
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username.equals("well4149")){
            return new CustomUserDetails(getUser());
        }
        return null;
    }
}
