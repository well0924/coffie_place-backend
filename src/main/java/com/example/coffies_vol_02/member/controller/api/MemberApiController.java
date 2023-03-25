package com.example.coffies_vol_02.member.controller.api;

import com.example.coffies_vol_02.config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.member.domain.dto.MemberDto;
import com.example.coffies_vol_02.member.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @GetMapping("/detail/{id}")
    public CommonResponse<?>memberDetail(@PathVariable("id")Integer userIdx){
        MemberDto.MemberResponseDto detail = memberService.findMemberById(userIdx);
        return new CommonResponse<>(HttpStatus.OK.value(),detail);
    }

    @PostMapping("/memberjoin")
    public CommonResponse<?>memberJoin(@Valid @RequestBody MemberDto.MemberCreateDto dto){
        int JoinResult = memberService.memberSave(dto);
        return new CommonResponse<>(HttpStatus.OK.value(),JoinResult);
    }

    @PatchMapping("/memberUpdate/{id}")
    public CommonResponse<?>memberUpdate(@PathVariable("id") Integer userIdx,@RequestBody MemberDto.MemberCreateDto dto){
        int UpdateResult = memberService.memberUpdate(userIdx,dto);
        return new CommonResponse<>(HttpStatus.OK.value(),UpdateResult);
    }

    @DeleteMapping("/memberDelete/{id}")
    public CommonResponse<?>memberDelete(@PathVariable("id") Integer userIdx){
        memberService.memberDelete(userIdx);
        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.K");
    }

    @GetMapping("/findid/{name}/{email}")
    public CommonResponse<?>findUserID(@PathVariable(value = "name") String userName,@PathVariable("email") String userEmail){
        String findUser =memberService.findByMembernameAndUseremail(userName,userEmail);
        return new CommonResponse<>(HttpStatus.OK.value(),findUser);
    }

    @GetMapping("/idduplicated/{id}")
    public CommonResponse<?>userIdDuplicated(@PathVariable("id")String userId){
        Boolean result = memberService.existsByUserId(userId);
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

    @GetMapping("/emailduplicated/{email}")
    public CommonResponse<?>userEmailDuplicated(@PathVariable("email")String userEmail){
        Boolean result = memberService.existByUserEmail(userEmail);
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

    @PatchMapping("/newpassword/{id}")
    public CommonResponse<?>passwordChange(@PathVariable("id")Integer id,@RequestBody MemberDto.MemberCreateDto dto){
        int updateResult = memberService.updatePassword(id,dto);
        return new CommonResponse<>(HttpStatus.OK.value(),updateResult);
    }
}
