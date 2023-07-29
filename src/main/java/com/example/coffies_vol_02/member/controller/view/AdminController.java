package com.example.coffies_vol_02.member.controller.view;

import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import com.example.coffies_vol_02.member.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@AllArgsConstructor
@RequestMapping("/page/admin")
public class AdminController {
    private final MemberService memberService;

    @GetMapping("/adminlist")
    public ModelAndView adminListPage(@PageableDefault(sort = "id",direction = Sort.Direction.DESC,size = 10) Pageable pageable,
                                      @RequestParam(value = "searchType",required = false) SearchType searchType,
                                      @RequestParam(value = "searchVal",required = false) String searchVal){
        ModelAndView mv = new ModelAndView();

        Page<MemberResponse> memberList = null;

        try{
            memberList = memberService.findAll(pageable);
            if(searchVal!=null){
                memberList = memberService.findByAllSearch(searchType,searchVal,pageable);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        mv.addObject("memberlist",memberList);
        mv.addObject("searchType",searchType);
        mv.addObject("searchVal",searchVal);

        mv.setViewName("/admin/adminlist");

        return mv;
    }


}
