package com.example.coffies_vol_02.member.controller.view;

import com.example.coffies_vol_02.member.domain.dto.MemberDto;
import com.example.coffies_vol_02.member.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@AllArgsConstructor
@RequestMapping("/page/login")
public class MemberViewController {
    private final MemberService memberService;

    @GetMapping("/memberjoin")
    public ModelAndView memberJoinPage(){

        ModelAndView mv = new ModelAndView();

        mv.setViewName("/login/memberjoin");

        return mv;
    }

    @GetMapping("/loginPage")
    public ModelAndView loginPage(@RequestParam(value="error",required = false)String error,@RequestParam(value="exception",required = false) String exception){
        ModelAndView mv = new ModelAndView();

        mv.addObject("error", error);
        mv.addObject("exception", exception);

        mv.setViewName("/login/loginpage");

        return mv;
    }

    @GetMapping("/tmpid")
    public ModelAndView findIdPage(){
        ModelAndView mv = new ModelAndView();

        mv.setViewName("/login/searchId");

        return mv;
    }

    @GetMapping("/modify/{id}")
    public ModelAndView memberModifyPage(@PathVariable("id")Integer useridx){
        ModelAndView mv = new ModelAndView();

        MemberDto.MemberResponseDto dto = null;

        try{
            dto = memberService.findMember(useridx);
        }catch(Exception e){
            e.printStackTrace();
        }

        mv.addObject("detail",dto);
        mv.setViewName("/login/membermodify");

        return mv;
    }
}
