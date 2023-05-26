package com.example.coffies_vol_02.member.domain.dto.response;

import com.example.coffies_vol_02.member.domain.Role;

public record MemberResponse(Integer id, String userId, String password, String memberName, String userPhone, String userGender, String userAge, String userEmail, String userAddr1, String userAddr2,
                             Role role) {

}
