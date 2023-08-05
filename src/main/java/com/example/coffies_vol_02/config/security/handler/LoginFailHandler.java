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

    private CustomUserDetails member;

    public static final int MAX_FAILED_ATTEMPTS = 3;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        //에러 매시지
        String errorMsg = "";
        String userId = request.getParameter("userId");

        try {
            //로그인 실패시 에러 메시지 출력
            LoginFailErrorMessages(errorMsg,userId,exception);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.onAuthenticationFailure(request, response, exception);
    }

    //로그인 실패시 에러 메시지 출력
    private String LoginFailErrorMessages(String Msg, String userId,AuthenticationException exception) throws Exception {

        member = (CustomUserDetails) customUserDetailService.loadUserByUsername(userId);

        //비밀번호가 맞지 않는 경우
        if (exception instanceof BadCredentialsException) {
            loginFailureCount(userId,exception);
            Msg = "비밀번호가 맞지 않습니다.";
        } else if (exception instanceof InternalAuthenticationServiceException) {
            loginFailureCount(userId,exception);
            //InternalAuthenticationServiceException: 인증처리에 문제가 있는 경우
            Msg = "회원이 존재하지 않습니다.";
        } else if (exception instanceof UsernameNotFoundException) {
            //UsernameNotFoundException: 회원아이디가 없는 경우
            Msg = "계정이 존재하지 않습니다. 회원가입 진행 후 로그인 해주세요.";
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            //AuthenticationCredentialsNotFoundException: 인증이 거부되는 경우
            Msg = "인증 요청이 거부되었습니다. 관리자에게 문의하세요.";        }
        else {
            Msg = "알 수 없는 이유로 로그인에 실패하였습니다 관리자에게 문의하세요.";
        }

        log.info(exception);
        log.info(exception.getMessage());

        Msg = URLEncoder.encode(Msg, "UTF-8");

        setDefaultFailureUrl("/page/login/loginPage?error=true&exception="+Msg);
        return Msg;
    }

    protected void loginFailureCount(String userId,AuthenticationException exception){

        memberService.increaseFailAttempts(userId);

        int failCount= memberService.FailAttemptsConfirm(userId);

        if(failCount==3){
            memberService.lock();
            exception = new LockedException("회원의 계정이 정지되었습니다.");
        } else if (!member.getMember().getAccountNonLocked()) {
            if(memberService.unlockWhenTimeExpired(member.getMember())){
                exception = new LockedException("Your account has been unlocked. Please try to login again.");
            }
        }
    }
}
