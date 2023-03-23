package com.example.coffies_vol_02.member.controller.api;

import com.example.coffies_vol_02.member.domain.dto.MemberDto;
import com.example.coffies_vol_02.member.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {
    private final MemberService memberService;

    @GetMapping("/list")
    public ResponseEntity<?>memberList(@PageableDefault(sort = "id",direction = Sort.Direction.DESC,size = 5) Pageable pageable){
        Page<MemberDto.MemberResponseDto> list = memberService.findAll(pageable);
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?>memberDetail(@PathVariable("id")Integer userIdx){
        MemberDto.MemberResponseDto detatil = memberService.findMemberById(userIdx);
        return new ResponseEntity<>(detatil,HttpStatus.OK);
    }

    @PostMapping("/memberjoin")
    public ResponseEntity<?>memberJoin(@RequestBody MemberDto.MemberCreateDto dto){
        int JoinResult = memberService.memberSave(dto);
        return new ResponseEntity<>(JoinResult,HttpStatus.OK);
    }

    @PutMapping("/memberUpdate/{id}")
    public ResponseEntity<?>memberUpdate(@PathVariable("id") Integer userIdx, @RequestBody MemberDto.MemberCreateDto dto){
        int UpdateResult = memberService.memberUpdate(userIdx,dto);
        return new ResponseEntity<>(UpdateResult,HttpStatus.OK);
    }

    @DeleteMapping("/memberDelete/{id}")
    public ResponseEntity<?>memberDelete(@PathVariable("id") Integer userIdx){
        memberService.memberDelete(userIdx);
        return new ResponseEntity<>("Delete O.K",HttpStatus.OK);
    }

    @GetMapping("/findid/{name}/{email}")
    public ResponseEntity<?>findUserID(@PathVariable(value = "name") String userName,@PathVariable("email") String userEmail){
        String findUser =memberService.findByMembernameAndUseremail(userName,userEmail);
        return new ResponseEntity<>(findUser,HttpStatus.OK);
    }

    @GetMapping("/idduplicated/{id}")
    public ResponseEntity<?>userIdDuplicated(@PathVariable("id")String userId){
        Boolean result = memberService.existsByUserId(userId);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping("/emailduplicated/{email}")
    public ResponseEntity<?>userEmailDuplicated(@PathVariable("email")String userEmail){
        Boolean result = memberService.existByUserEmail(userEmail);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @PutMapping("/newpassword/{id}")
    public ResponseEntity<?>passwordChange(@PathVariable("id")Integer id,@RequestBody MemberDto.MemberCreateDto dto){
        int updateResult = memberService.updatePassword(id,dto);
        return new ResponseEntity<>(updateResult,HttpStatus.OK);
    }


}
