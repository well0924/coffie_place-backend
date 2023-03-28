package com.example.coffies_vol_02.member.controller.api;

import com.example.coffies_vol_02.config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.member.domain.dto.MemberDto;
import com.example.coffies_vol_02.member.service.MemberService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.PrintWriter;
import java.util.List;


@Api(tags = "member api controller")
@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {
    private final MemberService memberService;

    @GetMapping("/list")
    public CommonResponse<?> memberList(@PageableDefault(sort = "id",direction = Sort.Direction.DESC,size = 5) Pageable pageable){
        Page<MemberDto.MemberResponseDto> list = memberService.findAll(pageable);
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }
    @GetMapping("/detail/{user_idx}")
    public CommonResponse<?>memberDetail(@PathVariable("user_idx")Integer userIdx){
        MemberDto.MemberResponseDto detail = memberService.findMemberById(userIdx);
        return new CommonResponse<>(HttpStatus.OK.value(),detail);
    }

    @PostMapping("/memberjoin")
    public CommonResponse<?>memberJoin(@Valid @RequestBody MemberDto.MemberCreateDto dto, BindingResult bindingResult){
        int JoinResult = memberService.memberSave(dto);
        return new CommonResponse<>(HttpStatus.OK.value(),JoinResult);
    }

    @PatchMapping("/memberUpdate/{user_idx}")
    public CommonResponse<?>memberUpdate(@PathVariable("user_idx") Integer userIdx,@RequestBody MemberDto.MemberCreateDto dto){
        int UpdateResult = memberService.memberUpdate(userIdx,dto);
        return new CommonResponse<>(HttpStatus.OK.value(),UpdateResult);
    }

    @DeleteMapping("/memberDelete/{user_idx}")
    public CommonResponse<?>memberDelete(@PathVariable("user_idx") Integer userIdx){
        memberService.memberDelete(userIdx);
        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.K");
    }

    @GetMapping("/findid/{user_name}/{user_email}")
    public CommonResponse<?>findUserID(@PathVariable(value = "user_name") String userName,@PathVariable("user_email") String userEmail){
        String findUser =memberService.findByMembernameAndUseremail(userName,userEmail);
        return new CommonResponse<>(HttpStatus.OK.value(),findUser);
    }

    @GetMapping("/idduplicated/{user_id}")
    public CommonResponse<?>userIdDuplicated(@PathVariable("user_id")String userId){
        Boolean result = memberService.existsByUserId(userId);
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

    @GetMapping("/emailduplicated/{user_email}")
    public CommonResponse<?>userEmailDuplicated(@PathVariable("user_email")String userEmail){
        Boolean result = memberService.existByUserEmail(userEmail);
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

    @PatchMapping("/newpassword/{user_id}")
    public CommonResponse<?>passwordChange(@PathVariable("user_id")Integer id,@RequestBody MemberDto.MemberCreateDto dto){
        int updateResult = memberService.updatePassword(id,dto);
        return new CommonResponse<>(HttpStatus.OK.value(),updateResult);
    }

    @GetMapping("/autocompetekeyword")
    public void memberNameAutoComplete(HttpServletRequest request, HttpServletResponse response)throws Exception{
        String searchValue = request.getParameter("searchValue");
        JSONArray arrayObj = memberService.autoSearch(searchValue);

        log.info(arrayObj);

        response.setCharacterEncoding("UTF-8");

        PrintWriter pw = response.getWriter();
        pw.print(arrayObj);
        pw.flush();
        pw.close();
    }

    @PostMapping("/selectdelete")
    public CommonResponse<?>MemberDelete(@RequestBody List<String> userId){
        memberService.selectMemberDelete(userId);
        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.k");
    }
}
