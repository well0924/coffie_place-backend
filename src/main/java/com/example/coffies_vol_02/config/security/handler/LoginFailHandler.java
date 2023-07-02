package com.example.coffies_vol_02.config.security.handler;

import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.member.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class LoginFailHandler extends SimpleUrlAuthenticationFailureHandler {
    private MemberService memberService;
    private MemberRepository memberRepository;

    public LoginFailHandler(MemberService memberService,MemberRepository memberRepository){
        this.memberService = memberService;
        this.memberRepository = memberRepository;
    }
    private static final int MAX_ATTEMPTS = 3; // 최대 실패 횟수

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        //에러 매시지
        String errorMsg = "";
        String userId = request.getParameter("userId");
        Optional<Member> user = memberRepository.findByUserId(userId);
        Member member = user.get();

        //로그인 횟수 제한 실패시 계정을 일시적으로 잠금.
        if(member!=null){
            if(member.isEnabled()&&member.getAccountNonLocked()){
                //비밀번호에 실패를 하는 경우 실패 횟수 증가(2회 까지)
                if(member.getFailedAttempt()<MAX_ATTEMPTS-1){
                    memberService.increaseFailAttempts(member);
                }else {
                    //최대 횟수를 넘은 경우 계정을 잠금
                    memberService.lock(member);
                    exception = new LockedException("로그인 3회 실패로 계정이 정지 되었습니다. 1시간후 정지가 해제 됩니다.");
                }
            //계정이 잠겨 있는 경우
            } else if (!member.getAccountNonLocked()) {
                if(memberService.unlockWhenTimeExpired(member)){
                    exception = new LockedException("계정정지가 해제되었습니다. 로그인을 해주에세요.");
                }
            }
        }
        //로그인 실패시 에러 메시지 출력
        LoginFailErrorMessages(errorMsg,exception);

        super.onAuthenticationFailure(request, response, exception);
    }

    //로그인 실패시 에러 메시지 출력
    private String LoginFailErrorMessages(String Msg,AuthenticationException exception) throws UnsupportedEncodingException {

        if (exception instanceof BadCredentialsException) {
            Msg = "아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해 주세요.";
            //InternalAuthenticationServiceException: 인증처리에 문제가 있는 경우
        } else if (exception instanceof InternalAuthenticationServiceException) {
            Msg = "내부적으로 발생한 시스템 문제로 인해 요청을 처리할 수 없습니다. 관리자에게 문의하세요.";
            //UsernameNotFoundException: 회원아이디가 없는 경우
        } else if (exception instanceof UsernameNotFoundException) {
            Msg = "계정이 존재하지 않습니다. 회원가입 진행 후 로그인 해주세요.";
            //AuthenticationCredentialsNotFoundException: 인증이 거부되는 경우
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            Msg = "인증 요청이 거부되었습니다. 관리자에게 문의하세요.";        }
        else {
            Msg = "알 수 없는 이유로 로그인에 실패하였습니다 관리자에게 문의하세요.";
        }

        log.info(exception);
        Msg = URLEncoder.encode(Msg, "UTF-8");

        setDefaultFailureUrl("/page/login/loginPage?error=true&exception="+Msg);
        return Msg;
    }
}
