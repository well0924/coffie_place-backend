package com.example.coffies_vol_02.member.service;

import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetailService;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.dto.request.LoginDto;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

@Log4j2
@Service
@AllArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    private final CustomUserDetailService customUserDetailService;

    @Transactional
    public String login(LoginDto loginDto, HttpSession httpSession){
        log.info("service");
        CustomUserDetails  customUserDetails = (CustomUserDetails) customUserDetailService.loadUserByUsername(loginDto.getUserId());
        log.info("service user details:::"+customUserDetails.getMember());

        if(customUserDetails == null ||!encoder.matches(loginDto.getPassword(),customUserDetails.getMember().getPassword())){
            throw new CustomExceptionHandler(ERRORCODE.PASSWORD_NOT_MATCH);
        }

        httpSession.setAttribute("member",customUserDetails.getMember());

        log.info("인증정보::"+httpSession.getAttribute("member"));

        return httpSession.getId();
    }

    public void logout(HttpSession httpSession){
        httpSession.removeAttribute("member");
    }
}
