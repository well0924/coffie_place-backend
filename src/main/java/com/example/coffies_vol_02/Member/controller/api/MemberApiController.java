package com.example.coffies_vol_02.Member.controller.api;

import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.Config.Exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.Member.domain.dto.MemberDto;
import com.example.coffies_vol_02.Member.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@Api(tags = "Member api",value = "회원 관련 api 컨트롤러")
@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {
    private final MemberService memberService;

    @Operation(summary = "회원 목록 api",description = "회원전체 목록을 출력한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원목록 조회성공", content = @Content(schema = @Schema(implementation = MemberDto.MemberResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "회원목록이 없음", content = @Content(schema = @Schema(implementation = CustomExceptionHandler.class)))
    })
    @GetMapping("/list")
    public CommonResponse<?> memberList(@PageableDefault(sort = "id",direction = Sort.Direction.DESC,size = 5) Pageable pageable){
        Page<MemberDto.MemberResponseDto> list = memberService.findAll(pageable);
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }
    @Operation(summary = "회원 단일 조회 api",description = "회원을 단일  조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원목록 조회성공", content = @Content(schema = @Schema(implementation = MemberDto.MemberResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "조회된 회원없음", content = @Content(schema = @Schema(implementation = CustomExceptionHandler.class)))
    })
    @GetMapping("/detail/{user_idx}")
    public CommonResponse<?>memberDetail(@PathVariable("user_idx")Integer userIdx){
        MemberDto.MemberResponseDto detail = memberService.findMemberById(userIdx);
        return new CommonResponse<>(HttpStatus.OK.value(),detail);
    }

    @Operation(summary = "회원가입 api",description = "회원가입 기능.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "회원가입 성공")})
    @PostMapping("/memberjoin")
    public CommonResponse<?>memberJoin(@Valid @RequestBody MemberDto.MemberCreateDto dto, BindingResult bindingResult){
        int JoinResult = memberService.memberSave(dto);
        return new CommonResponse<>(HttpStatus.OK.value(),JoinResult);
    }
    @Operation(summary = "회원수정 api",description = "회원수정 기능")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "회원수정 성공")})
    @PatchMapping("/memberUpdate/{user_idx}")
    public CommonResponse<?>memberUpdate(@PathVariable("user_idx") Integer userIdx,@RequestBody MemberDto.MemberCreateDto dto){
        int UpdateResult = memberService.memberUpdate(userIdx,dto);
        return new CommonResponse<>(HttpStatus.OK.value(),UpdateResult);
    }
    @Operation(summary = "회원삭제 api",description = "회원 삭제기능")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "회원삭제성공")})
    @DeleteMapping("/memberDelete/{user_idx}")
    public CommonResponse<?>memberDelete(@PathVariable("user_idx") Integer userIdx){
        memberService.memberDelete(userIdx);
        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.K");
    }

    @Operation(summary = "회원 아이디 찾기 api",description = "회원아이디 찾기 페이지에서 이름과 이메일을 입력해서 회원아이디를 조회하는 api")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원아이디 조회성공"),
            @ApiResponse(responseCode = "404", description = "회원아이디가 없음", content = @Content(schema = @Schema(implementation = CustomExceptionHandler.class)))
    })
    @GetMapping("/findid/{user_name}/{user_email}")
    public CommonResponse<?>findUserID(@PathVariable(value = "user_name")String userName, @PathVariable("user_email")String userEmail){
        String findUser =memberService.findByMembernameAndUseremail(userName,userEmail);
        return new CommonResponse<>(HttpStatus.OK.value(),findUser);
    }
    @Operation(summary = "회원 아이디 중복 api",description = "회원가입 페이지에서 아이디 중복기능")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원id 사용가능")
    })
    @GetMapping("/idduplicated/{user_id}")
    public CommonResponse<?>userIdDuplicated(@PathVariable("user_id")String userId){
        Boolean result = memberService.existsByUserId(userId);
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

    @Operation(summary = "회원 이메일 중복 api")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 사용가능")
    })
    @GetMapping("/emailduplicated/{user_email}")
    public CommonResponse<?>userEmailDuplicated(@PathVariable("user_email")String userEmail){
        Boolean result = memberService.existByUserEmail(userEmail);
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }
    @Operation(summary = "회원비밀번호 변경 api",description = "회원 비밀번호 변경 페이지에서 비밀번호 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원비밀번호 변경 성공")
    })
    @PatchMapping("/newpassword/{user_id}")
    public CommonResponse<?>passwordChange(@PathVariable("user_id")Integer id,@RequestBody MemberDto.MemberCreateDto dto){
        int updateResult = memberService.updatePassword(id,dto);
        return new CommonResponse<>(HttpStatus.OK.value(),updateResult);
    }


    @GetMapping("/autocompetekeyword")
    public void memberNameAutoComplete(HttpServletRequest request, HttpServletResponse response)throws Exception{
        String searchValue = request.getParameter("searchValue");

        JSONArray jsonArray = (JSONArray) memberService.autoSearch(searchValue);
        response.setCharacterEncoding("UTF-8");

        PrintWriter pw = response.getWriter();
        pw.print(jsonArray);
        pw.flush();
        pw.close();
    }

    @Operation(summary = "회원선택삭제 api",description = "어드민페이지에서 회원 선택삭제하는 기능")
    @ApiResponse(responseCode = "200",description = "회원선택 삭제")
    @PostMapping("/selectdelete")
    public CommonResponse<?>MemberDelete(@RequestBody List<String> userId){
        memberService.selectMemberDelete(userId);
        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.k");
    }
}
