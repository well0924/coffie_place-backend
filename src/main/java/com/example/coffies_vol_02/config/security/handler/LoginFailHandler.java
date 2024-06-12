package com.example.coffies_vol_02.config.security.handler;

import com.example.coffies_vol_02.config.security.auth.CustomUserDetailService;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.member.service.MemberService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@Log4j2
@Component
public class LoginFailHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private MemberService memberService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        //에러 매시지
        String userId = request.getParameter("userId");
        log.info("입력 아이디:"+userId);
        //로그인 실패시 에러 메시지 출력
        CustomUserDetails member = (CustomUserDetails) customUserDetailService.loadUserByUsername(userId);
        String Msg = null;
        
        //로그인 실패시 잠금기능(3회실패시)
        memberService.loginFailed(userId);
        
        //로그인 실패시 에러 메시지
        if (exception instanceof InternalAuthenticationServiceException) {
            //InternalAuthenticationServiceException: 인증처리에 문제가 있는 경우
            Msg = String.valueOf(new InternalAuthenticationServiceException("회원이 존재하지 않습니다."));
        } else if (exception instanceof UsernameNotFoundException) {
            //UsernameNotFoundException: 회원아이디가 없는 경우
            Msg = String.valueOf(new UsernameNotFoundException("계정이 존재하지 않습니다. 회원가입 진행 후 로그인 해주세요."));
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            //AuthenticationCredentialsNotFoundException: 인증이 거부되는 경우
            Msg = String.valueOf(new AuthenticationCredentialsNotFoundException("인증 요청이 거부되었습니다. 관리자에게 문의하세요."));
        } else if (exception instanceof LockedException) {
            //
            Msg = String.valueOf(new LockedException("비밀번호를 3회 틀렸습니다. 계정이 잠겼습니다. 24시간 후에 다시 로그인해주세요."));
        } else if(exception instanceof BadCredentialsException){
            //
            Msg = String.valueOf(new BadCredentialsException("비밀번호나 아이디가 틀렸습니다."));
        }

        //계정이 잠겼는지 여부를 확인
        if (memberService.isAccountLocked(userId)) {
            //잠겼을 경우
            response.sendRedirect("/page/login/loginPage?locked");
        } else {
            response.sendRedirect("/page/login/loginPage?error");
        }

        Msg = URLEncoder.encode(Msg, "UTF-8");
        log.info(exception);
        log.info(exception.getMessage());
        //setDefaultFailureUrl("/page/login/loginPage?error="+Msg);

        super.onAuthenticationFailure(request, response, exception);
    }

}
