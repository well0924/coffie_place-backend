package com.example.coffies_vol_02.config.security;

import com.example.coffies_vol_02.config.security.auth.CustomUserDetailService;
import com.example.coffies_vol_02.config.security.handler.LoginFailHandler;
import com.example.coffies_vol_02.config.security.handler.LoginSuccessHandler;
import lombok.AllArgsConstructor;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

@Log4j2
@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
public class SecurityConfig {
    private final CustomUserDetailService customUserDetailService;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailHandler loginFailHandler;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web
                .httpFirewall(defaultFireWell())
                .ignoring()
                .antMatchers("/images/**", "/js/**","/font/**", "/webfonts/**",
                        "/main/**", "/webjars/**", "/dist/**", "/plugins/**", "/css/**","/favicon.ico","/h2-console/**");
    }

    @Bean
    public AuthenticationProvider authProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailService);
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return provider;
    }

    private static final String[] PERMIT_URL_ARRAY = {
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

        log.info("security config...");

        http
            //csrf 토큰 비활성화
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/**").permitAll()
            .antMatchers(PERMIT_URL_ARRAY).permitAll()
            .anyRequest().authenticated();

        http
            .formLogin()
            .loginPage("/page/login/loginPage")
            .usernameParameter("userId")
            .passwordParameter("password")
            .loginProcessingUrl("/login/action").permitAll()
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailHandler);

        http
            .logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/page/login/loginPage")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID");

        return http.build();
    }

    @Bean
    public HttpFirewall defaultFireWell(){
        return new DefaultHttpFirewall();
    }

}
