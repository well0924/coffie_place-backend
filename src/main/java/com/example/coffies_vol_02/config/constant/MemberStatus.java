package com.example.coffies_vol_02.config.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberStatus {

    USER_LOCK("USER_LOCK"),
    NON_USER_LOCK("NON_USER_LOCK");

    private final String value;
}
