package com.example.coffies_vol_02.member.controller.view;

import com.example.coffies_vol_02.member.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class MemberViewController {
    private final MemberService memberService;
}
