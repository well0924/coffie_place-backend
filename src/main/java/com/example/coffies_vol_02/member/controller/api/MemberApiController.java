package com.example.coffies_vol_02.member.controller.api;

import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.member.domain.dto.request.MemberRequest;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
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
import java.util.ArrayList;
import java.util.List;

@Api(tags = "Member api",value = "회원 관련 api 컨트롤러")
@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {
    private final MemberService memberService;

    @Operation(summary = "회원 목록 api", description = "회원전체 목록을 출력한다.")
    @GetMapping(path = "/list")
    public CommonResponse<Page<MemberResponse>> memberList(@ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC,size = 5) Pageable pageable){
        Page<MemberResponse> list = null;

        try{
            list = memberService.findAll(pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @Operation(summary = "회원 검색 api", description = "회원목록에서 검색을 한다.")
    @GetMapping(path = "/search")
    public CommonResponse<Page<MemberResponse>>memberSearch(@ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC) Pageable pageable, @RequestParam("searchVal") String searchVal){

        Page<MemberResponse> list = null;

        try{
            list = memberService.findByAllSearch(searchVal,pageable);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @Operation(summary = "회원 단일 조회 api", description = "회원을 단일 조회한다.")
    @GetMapping(path = "/detail/{user_idx}")
    public CommonResponse<MemberResponse>findMember(@PathVariable("user_idx")Integer userIdx){
        MemberResponse detail = memberService.findMemberRecord(userIdx);

        try{
            detail = memberService.findMemberRecord(userIdx);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),detail);
    }

    @Operation(summary = "회원가입 api",description = "회원가입 기능.")
    @PostMapping(path = "/join")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<?>memberCreate(@Valid @RequestBody MemberRequest dto, BindingResult bindingResult){

        try{
            memberService.memberCreate(dto);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(HttpStatus.OK.is2xxSuccessful()){
            return new CommonResponse<>(HttpStatus.OK.value(),"회원가입이 완료되었습니다.");
        }else if(HttpStatus.BAD_REQUEST.is4xxClientError()) {
            return new CommonResponse<>(HttpStatus.BAD_REQUEST.value(), "회원가입에 실패했습니다.");
        }else{
            return new CommonResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 문제가 있습니다.");
        }
    }

    @ApiOperation(value = "회원수정 api")
    @PatchMapping(path = "/update/{user_idx}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<?>memberUpdate(@PathVariable("user_idx") Integer userIdx,@RequestBody MemberRequest dto){
        try{
            memberService.memberUpdate(userIdx,dto);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(HttpStatus.OK.is2xxSuccessful()){
            return new CommonResponse<>(HttpStatus.OK.value(),"회원정보가 수정되었습니다.");
        }else if(HttpStatus.BAD_REQUEST.is4xxClientError()) {
            return new CommonResponse<>(HttpStatus.BAD_REQUEST.value(), "회원수정에 실패했습니다.(잘못된 요청입니다.)");
        }else{
            return new CommonResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 문제가 있습니다.");
        }
    }

    @ApiOperation(value = "회원삭제 api")
    @DeleteMapping(path = "/delete/{user_idx}")
    public CommonResponse<?>memberDelete(@PathVariable("user_idx") Integer userIdx){
        try{
            memberService.memberDelete(userIdx);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(HttpStatus.OK.is2xxSuccessful()){
            return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.K");
        }else if(HttpStatus.BAD_REQUEST.is4xxClientError()){
            return new CommonResponse<>(HttpStatus.BAD_REQUEST.value(), "회원삭제에 실패했습니다.(잘못된 요청입니다.)");
        }else{
            return new CommonResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 문제가 있습니다.");
        }
    }

    @ApiOperation(value = "회원 아이디 찾기 api")
    @GetMapping(path = "/find-id/{user_name}/{user_email}")
    public CommonResponse<String>findUserId(@PathVariable(value = "user_name")String userName, @PathVariable("user_email")String userEmail){
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
    public CommonResponse<Integer>passwordUpdate(@PathVariable("user_id")Integer id,@RequestBody MemberRequest dto){
        int updateResult = 0;

        try{
            updateResult = memberService.updatePassword(id,dto);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),updateResult);
    }

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

    @Operation(summary = "회원 검색 자동완성",description = "어드민 페이지에서 회원을 검색할 때 검색어를 자동완성기능")
    @GetMapping(path = "/autocomplete/{id}")
    public  CommonResponse<List<String>>memberAutoComplete(@PathVariable(value = "id") String userId){
        List<String>list = new ArrayList<>();

        try{
            list = memberService.memberAutoSearch(userId);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }
}
