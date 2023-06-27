package com.example.coffies_vol_02.config.security.handler;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
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

    private static final int MAX_ATTEMPTS = 3; // 최대 실패 횟수
    private static final int LOCK_DURATION = 300; // 잠금 시간(초)

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorMsg;

        //BadCredentialsException: 비밀번호가 일치하지 않을경우
        if (exception instanceof BadCredentialsException) {
            errorMsg = "아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해 주세요.";
            //InternalAuthenticationServiceException: 인증처리에 문제가 있는 경우
        } else if (exception instanceof InternalAuthenticationServiceException) {
            errorMsg = "내부적으로 발생한 시스템 문제로 인해 요청을 처리할 수 없습니다. 관리자에게 문의하세요.";
        } else if (exception instanceof UsernameNotFoundException) {
            errorMsg = "계정이 존재하지 않습니다. 회원가입 진행 후 로그인 해주세요.";
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            errorMsg = "인증 요청이 거부되었습니다. 관리자에게 문의하세요.";        }
        else {
            errorMsg = "알 수 없는 이유로 로그인에 실패하였습니다 관리자에게 문의하세요.";
        }
        log.info(exception);
        errorMsg = URLEncoder.encode(errorMsg, "UTF-8");

        setDefaultFailureUrl("/page/login/loginPage?error=true&exception="+errorMsg);

        super.onAuthenticationFailure(request, response, exception);
    }

    private void increaseFailedAttempts(HttpServletRequest request) {
        // 사용자 계정의 실패 횟수 증가 로직 구현
    }

    private int getFailedAttempts(HttpServletRequest request) {
        // 사용자 계정의 실패 횟수 조회 로직 구현
        return 0; // 예시로 0을 반환하도록 설정됨
    }

    private void lockAccount(HttpServletRequest request) {
        // 사용자 계정 잠금 로직 구현
    }
}
