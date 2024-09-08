package com.example.coffies_vol_02.member.controller.api;

import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.email.EmailService;
import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.redis.RedisService;
import com.example.coffies_vol_02.member.domain.dto.request.LoginDto;
import com.example.coffies_vol_02.member.domain.dto.request.MemberRequest;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import com.example.coffies_vol_02.member.service.AuthService;
import com.example.coffies_vol_02.member.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Api(tags = "Member api",value = "회원 관련 api 컨트롤러")
@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {

    private final MemberService memberService;

    private final AuthService authService;

    private final EmailService emailService;

    private final RedisService redisService;

    @Operation(summary = "회원 로그인 api", description = "redis session을 활용해서 로그인")
    @PostMapping(value = "/login",consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse<?>loginProc(@RequestBody LoginDto loginDto, HttpSession httpSession){
        log.info("로그인??");
        String sessionId = authService.login(loginDto,httpSession);
        log.info(sessionId);
        log.info("session::::"+httpSession.getAttribute("member"));
        return new CommonResponse<>(HttpStatus.OK,sessionId);
    }

    @Operation(summary = "회원 로그아웃 api", description = "redis session을 활용해서 로그아웃")
    @PostMapping("/logout")
    public CommonResponse<String>logout(HttpSession httpSession){
        authService.logout(httpSession);
        httpSession.invalidate();
        return new CommonResponse<>(HttpStatus.OK,"log-out");
    }

    @Operation(summary = "회원 목록 api", description = "회원전체 목록을 출력한다.")
    @GetMapping(path = "/")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<Page<MemberResponse>> memberList(
            @ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC,size = 5) Pageable pageable){

        Page<MemberResponse> list = memberService.findAll(pageable);

        return new CommonResponse<>(HttpStatus.OK,list);
    }

    @Operation(summary = "회원 검색 api", description = "회원목록에서 검색을 한다.")
    @GetMapping(path = "/search")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<?>memberSearch(@ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC) Pageable pageable,
                                        @RequestParam(value = "searchType",required = false) String searchType,
                                        @Parameter(name = "searchVal",description = "회원 검색에 필요한 검색어",in = ParameterIn.QUERY)
                                        @RequestParam(value = "searchVal",required = false) String searchVal){

        Page<MemberResponse> list = memberService.findByAllSearch(SearchType.valueOf(searchType),searchVal,pageable);

        if(searchVal==null|| searchVal.isEmpty() ||searchType ==null|| searchType.isEmpty()){
            return new CommonResponse<>(HttpStatus.OK, ERRORCODE.NOT_SEARCH_VALUE);
        }

        return new CommonResponse<>(HttpStatus.OK,list);
    }

    @Operation(summary = "회원 단일 조회 api", description = "회원을 단일 조회한다.")
    @GetMapping(path = "/{user-idx}")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<MemberResponse>findMember(
            @Parameter(name = "user-idx",description = "회원의 번호",required = true,in = ParameterIn.PATH)
            @PathVariable("user-idx")Integer userIdx){

        MemberResponse detail = memberService.findByMember(userIdx);

        return new CommonResponse<>(HttpStatus.OK,detail);
    }

    @Operation(summary = "회원가입 api", description = "회원가입페이지에서 회원가입을 한다.")
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<?>memberCreate(@Valid @RequestBody MemberRequest dto, BindingResult bindingResult){

        memberService.memberCreate(dto);

        if(HttpStatus.OK.is2xxSuccessful()){
            return new CommonResponse<>(HttpStatus.OK,"회원가입이 완료되었습니다.");
        }else if(HttpStatus.BAD_REQUEST.is4xxClientError()) {
            return new CommonResponse<>(HttpStatus.BAD_REQUEST, "회원가입에 실패했습니다.");
        }else{
            return new CommonResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 있습니다.");
        }
    }

    @Operation(summary = "회원수정 api", description = "어드민 페이지 및 마이페이지에서 회원 정보를 수정")
    @PatchMapping(path = "/{user-idx}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<?>memberUpdate(
            @Parameter(name = "user-idx",description = "회원의 번호",required = true,in = ParameterIn.PATH)
            @PathVariable("user-idx") Integer userIdx,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,description = "회원 수정에 필요한 dto")
            @RequestBody MemberRequest dto){

        memberService.memberUpdate(userIdx,dto);

        if(HttpStatus.OK.is2xxSuccessful()){
            return new CommonResponse<>(HttpStatus.OK.value(),"회원정보가 수정되었습니다.");
        }else if(HttpStatus.BAD_REQUEST.is4xxClientError()) {
            return new CommonResponse<>(HttpStatus.BAD_REQUEST, "회원수정에 실패했습니다.(잘못된 요청입니다.)");
        }else{
            return new CommonResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,ERRORCODE.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "회원삭제 api", description = "어드민 페이지 및 마이페이지에서 회원삭제 및 탈퇴 기능")
    @DeleteMapping(path = "/{user-idx}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<?>memberDelete(
            @Parameter(name = "user-idx",description = "회원의 번호",required = true,in = ParameterIn.PATH)
            @PathVariable("user-idx") Integer userIdx){

        memberService.memberDelete(userIdx);

        if(HttpStatus.OK.is2xxSuccessful()){
            return new CommonResponse<>(HttpStatus.NO_CONTENT,"Delete O.K");
        }else if(HttpStatus.BAD_REQUEST.is4xxClientError()){
            return new CommonResponse<>(HttpStatus.BAD_REQUEST, "회원삭제에 실패했습니다.(잘못된 요청입니다.)");
        }else{
            return new CommonResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,ERRORCODE.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "회원 아이디 찾기 api", description = "회원아이디 찾기 화면에서 아이디 찾기",
            responses = {
        @ApiResponse(responseCode = "200",description = "정상적으로 회원의 아이디를 찾는 경우",content = @Content(mediaType = "application/json"))
    })
    @GetMapping(path = "/find-id/{user-name}/{user-email}")
    public CommonResponse<String>findUserId(@Parameter(name = "user-name",description = "회원 이름",required = true,in = ParameterIn.PATH)
                                            @PathVariable(value = "user-name")String userName,
                                            @Parameter(name = "user-email",description = "회원의 이메일",required = true,in = ParameterIn.PATH)
                                            @PathVariable("user-email")String userEmail){

        String findUser = memberService.findUserId(userName,userEmail);

        return new CommonResponse<>(HttpStatus.OK,findUser);
    }

    @Operation(summary = "회원 아이디 중복 api", description = "회원가입 페이지에서 아이디 중복기능")
    @GetMapping(path = "/id-check/{user-id}")
    public CommonResponse<Boolean>memberIdCheck(
            @Parameter(name = "user-id",description = "회원의 아이디",required = true,in = ParameterIn.PATH)
            @PathVariable("user-id")String userId){

        boolean result = memberService.memberIdCheck(userId);

        return new CommonResponse<>(HttpStatus.OK,result);
    }

    @Operation(summary = "회원 이메일 중복 api",description = "회원가입 화면에서 회원 이메일 중복여부 확인")
    @GetMapping(path = "/email-check/{user-email}")
    public CommonResponse<Boolean>memberEmailCheck(@Parameter(name = "user-email",description = "회원의 이메일",required = true,in = ParameterIn.PATH) @PathVariable("user-email")String userEmail){

        boolean result = memberService.memberEmailCheck(userEmail);

        return new CommonResponse<>(HttpStatus.OK,result);
    }

    @Operation(summary = "회원비밀번호 변경 api", description = "회원 비밀번호 변경 페이지에서 비밀번호 변경")
    @PatchMapping(path = "/password/{user-id}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>passwordUpdate(@Parameter(name = "user-idx",description = "회원의 번호",required = true,in = ParameterIn.PATH) 
                                                 @PathVariable("user-id")Integer id,
                                                 @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                         description = "비밀번호 변경에 필요한 dto",required = true)
                                                 @RequestBody MemberRequest dto){

        int updateResult = memberService.updatePassword(id,dto);

        return new CommonResponse<>(HttpStatus.OK,updateResult);
    }

    @Operation(summary = "회원선택삭제 api", description = "어드민페이지에서 회원 선택삭제하는 기능",responses = {
            @ApiResponse(responseCode = "204",description = "정상적으로 삭제를 하는 경우")
    })
    @PostMapping(path = "/select-delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<?>selectMemberDelete(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "선택 삭제시에 필요한 회원번호",required = true)
            @RequestBody List<String> userId){

        memberService.selectMemberDelete(userId);

        return new CommonResponse<>(HttpStatus.NO_CONTENT,"Delete O.k");
    }

    @Operation(summary = "회원 검색 자동완성",description = "어드민 페이지에서 회원을 검색할 때 검색어를 자동완성기능",responses = {
            @ApiResponse(responseCode = "200",description = "정상적으로 출력이되는 경우",content = @Content(mediaType = "application/json"))
    })
    @GetMapping(path = "/autocomplete")
    public  CommonResponse<List<String>>memberAutoComplete(@Parameter(name = "userId",description = "회원의 아이디",required = true,in = ParameterIn.QUERY)
                                                           @RequestParam(value = "userId") String userId){

        List<String>list = redisService.memberAutoSearch(userId);
        log.info(list);
        return new CommonResponse<>(HttpStatus.OK,list);
    }

    @Operation(summary = "이메일 인증",description = "회원 가입 페이지에서 이메일 인증 하는 기능.")
    @PostMapping("/email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?>emailTest(@RequestParam(value = "userEmail") String userEmail) throws Exception {
        emailService.sendSimpleMessage(userEmail);
        return ResponseEntity.ok(true);
    }

    @Operation(summary = "비밀번호 재설정 이메일 인증",description = "비밀번호 재설정 페이지에서 이메일 인증으로 인증 이메일을 보내는 기능")
    @PostMapping("/temporary-email")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?>temporaryEmail(@RequestParam(value = "userEmail") String userEmail)throws Exception{
        CompletableFuture<String> tmpPwFuture = emailService.sendTemporaryPasswordMessage(userEmail);
        String tmpPw = tmpPwFuture.get();
        log.info(tmpPw);
        return ResponseEntity.ok(tmpPw);
    }

}
