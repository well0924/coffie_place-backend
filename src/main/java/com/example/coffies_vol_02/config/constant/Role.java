package com.example.coffies_vol_02.config.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    ROLE_USER("ROLE_USER"),

    ROLE_ADMIN("ROLE_ADMIN");
    private String value;
}
