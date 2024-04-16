package com.example.coffies_vol_02.config.security.handler;

import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.dto.request.MemberRequest;
import com.example.coffies_vol_02.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStratgy = new DefaultRedirectStrategy();
    private static final String DEFAULT_URL= "/page/main/main";
    private static final String ADMIN_URL="/page/admin/adminlist";

    @Autowired
    private MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomUserDetails  customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        
        Member member = customUserDetails.getMember();
        //로그인 실패 횟수 리셋
        if(member.getFailedAttempt()>=0){
            memberService.resetFailedAttempts(member);
        }
        
        //로그인을 한 세션을 지우는 메서드
        clearAuthenticationAttributes(request);

        try {
            //권한별 페이지 이동
            redirectStrategy(request, response, authentication);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //로그인을 한 세션을 지우는 메서드
    private void clearAuthenticationAttributes(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if(session != null) {

            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        }
    }
    //로그인을 했을시 role에 의해서 특정url로 이동하는 메서드
    private void redirectStrategy(HttpServletRequest request,HttpServletResponse response,Authentication authentication)throws Exception {

        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if(savedRequest != null) {
            redirectStratgy.sendRedirect(request, response, DEFAULT_URL);
        }else {
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            //권한이 ADMIN이면 어드민 페이지로 이동
            if(roles.contains("ROLE_ADMIN")) {
                redirectStratgy.sendRedirect(request, response,ADMIN_URL);
            //권한이 USER이면 메인 페이지로 이동
            }else if(roles.contains("ROLE_USER")) {
                redirectStratgy.sendRedirect(request, response,DEFAULT_URL);
            }
        }
    }

}
