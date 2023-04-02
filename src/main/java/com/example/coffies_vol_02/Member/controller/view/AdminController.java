package com.example.coffies_vol_02.member.controller.view;

import com.example.coffies_vol_02.member.domain.dto.MemberDto;
import com.example.coffies_vol_02.member.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@AllArgsConstructor
@RequestMapping("/page/admin")
public class AdminController {
    private final MemberService memberService;

    @GetMapping("/adminlist")
    public ModelAndView adminListPage(@PageableDefault(sort = "id",direction = Sort.Direction.DESC,size = 10) Pageable pageable){
        ModelAndView mv = new ModelAndView();
        Page<MemberDto.MemberResponseDto> memberList = memberService.findAll(pageable);

        mv.addObject("memberlist",memberList);
        mv.setViewName("/admin/adminlist");

        return mv;
    }


}
