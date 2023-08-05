package com.example.coffies_vol_02.member.controller.api;

import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.member.domain.dto.request.MemberRequest;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import com.example.coffies_vol_02.member.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

    @ApiOperation(value = "회원 목록 api", notes = "회원전체 목록을 출력한다.")
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

    @ApiOperation(value = "회원 검색 api", notes = "회원목록에서 검색을 한다.")
    @GetMapping(path = "/list/search")
    public CommonResponse<?>memberSearch(@ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC) Pageable pageable,
                                                            @RequestParam(value = "searchType",required = false) SearchType searchType,
                                                            @Parameter(name = "searchVal",description = "회원 검색에 필요한 검색어",in = ParameterIn.QUERY)
                                                            @RequestParam(value = "searchVal",required = false) String searchVal){

        Page<MemberResponse> list = null;

        if(searchVal==null||searchVal.equals("")||searchType.getValue()==null||searchType.getValue().equals("")){
            return new CommonResponse<>(HttpStatus.OK.value(), ERRORCODE.NOT_SEARCH_VALUE.getMessage());
        }

        try{
            list = memberService.findByAllSearch(searchType,searchVal,pageable);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @ApiOperation(value = "회원 단일 조회 api", notes = "회원을 단일 조회한다.")
    @GetMapping(path = "/detail/{user-idx}")
    public CommonResponse<MemberResponse>findMember(@Parameter(name = "user-idx",description = "회원의 번호",required = true,in = ParameterIn.PATH) @PathVariable("user-idx")Integer userIdx){
        MemberResponse detail = memberService.findByMember(userIdx);

        try{
            detail = memberService.findByMember(userIdx);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),detail);
    }

    @ApiOperation(value = "회원가입 api", notes = "회원가입페이지에서 회원가입을 한다.")
    @PostMapping("/join")
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

    @ApiOperation(value = "회원수정 api", notes = "어드민 페이지 및 마이페이지에서 회원 정보를 수정")
    @PatchMapping(path = "/{user-idx}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<?>memberUpdate(@Parameter(name = "user-idx",description = "회원의 번호",required = true,in = ParameterIn.PATH) @PathVariable("user-idx") Integer userIdx,@RequestBody MemberRequest dto){

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

    @ApiOperation(value = "회원삭제 api", notes = "어드민 페이지 및 마이페이지에서 회원삭제 및 탈퇴 기능")
    @DeleteMapping(path = "/{user-idx}")
    public CommonResponse<?>memberDelete(@Parameter(name = "user-idx",description = "회원의 번호",required = true,in = ParameterIn.PATH) @PathVariable("user-idx") Integer userIdx){
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

    @ApiOperation(value = "회원 아이디 찾기 api", notes = "회원아이디 찾기 화면에서 아이디 찾기")
    @GetMapping(path = "/find-id/{user-name}/{user-email}")
    public CommonResponse<String>findUserId(@Parameter(name = "user-name",description = "회원 이름",required = true,in = ParameterIn.PATH) @PathVariable(value = "user-name")String userName,
                                            @Parameter(name = "user-email",description = "회원의 이메일",required = true,in = ParameterIn.PATH)@PathVariable("user-email")String userEmail){
        String findUser = "";

        try{
            findUser =memberService.findUserId(userName,userEmail);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),findUser);
    }

    @ApiOperation(value = "회원 아이디 중복 api", notes = "회원가입 페이지에서 아이디 중복기능")
    @GetMapping(path = "/id-check/{user-id}")
    public CommonResponse<Boolean>memberIdCheck(@Parameter(name = "user-id",description = "회원의 아이디",required = true,in = ParameterIn.PATH) @PathVariable("user-id")String userId){
        boolean result = false;

        try{
            result = memberService.memberIdCheck(userId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

    @ApiOperation(value = "회원 이메일 중복 api", notes = "회원가입 화면에서 회원 이메일 중복여부 확인")
    @GetMapping(path = "/email-check/{user-email}")
    public CommonResponse<Boolean>memberEmailCheck(@Parameter(name = "user-email",description = "회원의 이메일",required = true,in = ParameterIn.PATH) @PathVariable("user-email")String userEmail){
        boolean result = false;
        try{
            result = memberService.memberEmailCheck(userEmail);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

    @ApiOperation(value = "회원비밀번호 변경 api", notes = "회원 비밀번호 변경 페이지에서 비밀번호 변경")
    @PatchMapping(path = "/password/{user-id}")
    public CommonResponse<Integer>passwordUpdate(@Parameter(name = "user-idx",description = "회원의 번호",required = true,in = ParameterIn.PATH) @PathVariable("user-id")Integer id,@RequestBody MemberRequest dto){
        int updateResult = 0;

        try{
            updateResult = memberService.updatePassword(id,dto);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),updateResult);
    }

    @ApiOperation(value = "회원선택삭제 api", notes = "어드민페이지에서 회원 선택삭제하는 기능")
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

    @ApiOperation(value = "회원 검색 자동완성", notes = "어드민 페이지에서 회원을 검색할 때 검색어를 자동완성기능")
    @GetMapping(path = "/autocomplete")
    public  CommonResponse<List<String>>memberAutoComplete(@Parameter(name = "userId",description = "회원의 아이디",required = true,in = ParameterIn.QUERY)
                                                           @RequestParam(value = "userId") String userId){
        List<String>list = new ArrayList<>();

        try{
            list = memberService.memberAutoSearch(userId);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }
}
