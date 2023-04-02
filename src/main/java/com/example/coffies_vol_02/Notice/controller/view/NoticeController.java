package com.example.coffies_vol_02.Notice.controller.view;

import com.example.coffies_vol_02.Notice.service.NoticeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

}
