package com.example.coffies_vol_02.Config;

import com.example.coffies_vol_02.Config.security.auth.CustomUserDetailService;
import com.example.coffies_vol_02.Config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Member.domain.Role;
import com.example.coffies_vol_02.Member.domain.dto.MemberDto;
import com.example.coffies_vol_02.Member.service.MemberService;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@Configuration
public class WithMockSecurityConfig implements WithSecurityContextFactory<MockSecurityCustomUser> {
    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Override
    public SecurityContext createSecurityContext(MockSecurityCustomUser annotation) {

        String userId = annotation.userId();
        String password = annotation.userPassword();

        UserDetails userDetails = customUserDetailService.loadUserByUsername(userId);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        return context;
    }
}
