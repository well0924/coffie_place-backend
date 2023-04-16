package com.example.coffies_vol_02.Config.security.handler;

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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        clearAuthenticationAttributes(request);

        try {
            redirectStrategy(request, response, authentication);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if(session != null) {

            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        }
    }

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
