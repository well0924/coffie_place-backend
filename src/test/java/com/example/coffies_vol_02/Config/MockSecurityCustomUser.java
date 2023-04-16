package com.example.coffies_vol_02.Config;

import com.example.coffies_vol_02.Member.domain.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockSecurityConfig.class)
public @interface MockSecurityCustomUser {
    String userId() default "well4149";
    String userPassword() default "qwer41491!";

    String[] Roles() default {"USER"};

}
