package com.example.coffies_vol_02.notice.controller.view;

import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.service.AttachService;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.notice.domain.dto.response.NoticeResponse;
import com.example.coffies_vol_02.notice.service.NoticeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
@RequestMapping("/page/notice")
public class NoticeController {

    private final NoticeService noticeService;
    private final AttachService attachService;

    @GetMapping("/list")
    public ModelAndView noticeList( @RequestParam(value = "searchType",required = false) SearchType searchType,
                                    @RequestParam(value = "searchVal",required = false) String searchVal,
                                    @PageableDefault(size =5,sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
        ModelAndView mv = new ModelAndView();

        Page<NoticeResponse> list = null;

        try {
            list = noticeService.noticeAllList(pageable);
            //검색을 하는 경우
            if(searchType.getValue()!=null||searchVal!= null){
                list = noticeService.noticeSearchAll(searchType,searchVal,pageable);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        mv.addObject("noticelist",list);
        mv.addObject("searchVal",searchVal);
        mv.addObject("searchType",searchType);

        mv.setViewName("/notice/noticelist");

        return mv;
    }

    @GetMapping("/detail/{notice_id}")
    public ModelAndView noticeDetail(@PathVariable("notice_id") Integer noticeId){
        ModelAndView mv = new ModelAndView();

        NoticeResponse list = noticeService.findNotice(noticeId);
        List<AttachDto> attachList = new ArrayList<>();

        try{
            attachList = attachService.noticefilelist(noticeId);
        }catch (Exception e){
            e.printStackTrace();
        }

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

        NoticeResponse list = noticeService.findNotice(noticeId);

        mv.addObject("detail",list);
        mv.setViewName("/notice/noticemodify");

        return mv;
    }
}
