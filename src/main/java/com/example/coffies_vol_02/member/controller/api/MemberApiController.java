package com.example.coffies_vol_02.member.controller.api;

import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.member.domain.dto.request.MemberRequestDto;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponseDto;
import com.example.coffies_vol_02.member.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Member api",value = "회원 관련 api 컨트롤러")
@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {
    private final MemberService memberService;

    @Operation(summary = "회원 목록 api",description = "회원전체 목록을 출력한다.")
    @GetMapping(path = "/list")
    public CommonResponse<Page<MemberResponseDto>> memberList(@ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC,size = 5) Pageable pageable){
        Page<MemberResponseDto> list = null;

        try{
            list = memberService.findAll(pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @Operation(summary = "회원 검색 api",description = "회원목록에서 검색을 한다.")
    @GetMapping(path = "/search")
    public CommonResponse<Page<MemberResponseDto>>memberSearch(@ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC) Pageable pageable, @RequestParam("searchVal") String searchVal){

        Page<MemberResponseDto> list = null;

        try{
            list = memberService.findByAllSearch(searchVal,pageable);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @Operation(summary = "회원 단일 조회 api",description = "회원을 단일  조회한다.")
    @GetMapping(path = "/detail/{user_idx}")
    public CommonResponse<MemberResponseDto>findMember(@PathVariable("user_idx")Integer userIdx){
        MemberResponseDto detail = new MemberResponseDto();

        try{
            detail = memberService.findMember(userIdx);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),detail);
    }

    @Operation(summary = "회원가입 api",description = "회원가입 기능.")
    @PostMapping(path = "/join")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>memberCreate(@Valid @RequestBody MemberRequestDto dto, BindingResult bindingResult){
        Integer JoinResult = 0;

        try{
            JoinResult = memberService.memberCreate(dto);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),JoinResult);
    }

    @ApiOperation(value = "회원수정 api")
    @PatchMapping(path = "/update/{user_idx}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<?>memberUpdate(@PathVariable("user_idx") Integer userIdx,@RequestBody MemberRequestDto dto){
        Integer UpdateResult = 0;

        try{
            UpdateResult = memberService.memberUpdate(userIdx,dto);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),UpdateResult);
    }

    @ApiOperation(value = "회원삭제 api")
    @DeleteMapping(path = "/delete/{user_idx}")
    public CommonResponse<?>memberDelete(@PathVariable("user_idx") Integer userIdx){
        try{
            memberService.memberDelete(userIdx);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.K");
    }

    @ApiOperation(value = "회원 아이디 찾기 api")
    @GetMapping(path = "/find-id/{user_name}/{user_email}")
    public CommonResponse<?>findUserId(@PathVariable(value = "user_name")String userName, @PathVariable("user_email")String userEmail){
        String findUser = "";

        try{
            findUser =memberService.findUserId(userName,userEmail);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),findUser);
    }

    @Operation(summary = "회원 아이디 중복 api",description = "회원가입 페이지에서 아이디 중복기능")
    @GetMapping(path = "/id-check/{user_id}")
    public CommonResponse<Boolean>memberIdCheck(@PathVariable("user_id")String userId){
        boolean result = false;

        try{
            result = memberService.memberIdCheck(userId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

    @Operation(summary = "회원 이메일 중복 api")
    @GetMapping(path = "/email-check/{user_email}")
    public CommonResponse<Boolean>memberEmailCheck(@PathVariable("user_email")String userEmail){
        boolean result = false;
        try{
            result = memberService.memberEmailCheck(userEmail);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

    @Operation(summary = "회원비밀번호 변경 api",description = "회원 비밀번호 변경 페이지에서 비밀번호 변경")
    @PatchMapping(path = "/password/{user_id}")
    public CommonResponse<Integer>passwordUpdate(@PathVariable("user_id")Integer id,@RequestBody MemberRequestDto dto){
        int updateResult = 0;

        try{
            updateResult = memberService.updatePassword(id,dto);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),updateResult);
    }

   /* @ApiOperation(value = "회원 자동 검색")
    @GetMapping(path = "/auto-compete-keyword")
    public void memberNameAutoComplete(HttpServletRequest request, HttpServletResponse response){
        String searchValue;

        try {
            searchValue = request.getParameter("searchValue");

            JSONArray jsonArray = (JSONArray) memberService.autoSearch(searchValue);
            response.setCharacterEncoding("UTF-8");

            PrintWriter pw = response.getWriter();
            pw.print(jsonArray);
            pw.flush();
            pw.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }*/

    @Operation(summary = "회원선택삭제 api",description = "어드민페이지에서 회원 선택삭제하는 기능")
    @PostMapping(path = "/select-delete")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<?>selectMemberDelete(@RequestBody List<String> userId){
        try{
            memberService.selectMemberDelete(userId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.k");
    }
}
