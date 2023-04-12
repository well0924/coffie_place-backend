package com.example.coffies_vol_02.Notice.controller.view;

import com.example.coffies_vol_02.Attach.domain.AttachDto;
import com.example.coffies_vol_02.Attach.service.AttachService;
import com.example.coffies_vol_02.Notice.domain.dto.NoticeBoardDto;
import com.example.coffies_vol_02.Notice.service.NoticeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
@RequestMapping("/page/notice")
public class NoticeController {

    private final NoticeService noticeService;
    private final AttachService attachService;

    @GetMapping("/list")
    public ModelAndView noticeList(@PageableDefault(size =5,sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
        ModelAndView mv = new ModelAndView();
        Page<NoticeBoardDto.BoardResponseDto> list = noticeService.noticeList(pageable);
        mv.addObject("noticelist",list);
        mv.setViewName("/notice/noticelist");
        return mv;
    }

    @GetMapping("/detail/{notice_id}")
    public ModelAndView noticeDetail(@PathVariable("notice_id") Integer noticeId) throws Exception {
        ModelAndView mv = new ModelAndView();
        NoticeBoardDto.BoardResponseDto list = noticeService.noticeDetail(noticeId);
        List<AttachDto> attachList = attachService.noticefilelist(noticeId);

        mv.addObject("filelist",attachList);
        mv.addObject("detail",list);
        mv.setViewName("/notice/noticedetail");
        return mv;
    }

    @GetMapping("/writePage")
    public ModelAndView noticeWrite(){
        ModelAndView mv = new ModelAndView();

        String uuid = UUID.randomUUID().toString();
        String key = "notice_"+uuid.substring(0,uuid.indexOf("-"));

        mv.addObject("fileGroupId", key);
        mv.setViewName("/notice/noticewrite");

        return mv;
    }

    @GetMapping("/modifyPage/{notice_id}")
    public ModelAndView noticeModify(@PathVariable("notice_id")Integer noticeId){
        ModelAndView mv = new ModelAndView();

        NoticeBoardDto.BoardResponseDto list = noticeService.noticeDetail(noticeId);

        mv.addObject("noticelist",list);
        mv.setViewName("/notice/noticemodify");

        return mv;
    }
}
