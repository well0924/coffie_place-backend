package com.example.coffies_vol_02.config.security;

import com.example.coffies_vol_02.config.security.auth.CustomUserDetailService;
import com.example.coffies_vol_02.config.security.handler.LoginFailHandler;
import com.example.coffies_vol_02.config.security.handler.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

@Log4j2
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;

    private final FindByIndexNameSessionRepository sessionRepository;

    private final SecuritySessionExpiredStrategy securitySessionExpiredStrategy;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public LoginFailHandler loginFailHandler(){
        return new LoginFailHandler();
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler(){
        return new LoginSuccessHandler();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web
                .httpFirewall(defaultFireWell())
                .ignoring()
                .antMatchers("/images/**", "/js/**","/font/**", "/webfonts/**","/istatic/**",
                        "/main/**", "/webjars/**", "/dist/**", "/plugins/**", "/css/**","/favicon.ico","/h2-console/**");
    }

    @Bean
    public AuthenticationProvider authProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailService);
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return provider;
    }

    @Bean
    public SpringSessionBackedSessionRegistry sessionRegistry() {
        return new SpringSessionBackedSessionRegistry(this.sessionRepository);
    }

    private static final String[] PERMIT_URL_ARRAY = {
            "/**",
            /* swagger v2 */
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            /* swagger v3 */
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/member/login","/api/member/logout","/api/member/").permitAll()
            .antMatchers("/**").permitAll()
            .antMatchers(PERMIT_URL_ARRAY).permitAll()
                .anyRequest()
                .authenticated();
        //세션 설정
        http.sessionManagement(session -> session.sessionFixation(SessionManagementConfigurer
                        .SessionFixationConfigurer::changeSessionId)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/")
                .sessionRegistry(sessionRegistry())
                .expiredSessionStrategy(securitySessionExpiredStrategy))
        .addFilterBefore(new AuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer
                    .usernameParameter("userId")
                    .passwordParameter("password")
                    .loginProcessingUrl("/api/member/login")
                    .successHandler(loginSuccessHandler())
                    .failureHandler(loginFailHandler()));
        http
            .csrf(AbstractHttpConfigurer::disable)
            .rememberMe(AbstractHttpConfigurer::disable)
            .logout(logout ->logout
                    .logoutUrl("/api/member/logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessUrl("/page/main/main"));
        return http.build();
    }

    @Bean
    public HttpFirewall defaultFireWell(){
        return new DefaultHttpFirewall();
    }

}
