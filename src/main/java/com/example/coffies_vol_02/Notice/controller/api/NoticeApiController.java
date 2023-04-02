package com.example.coffies_vol_02.Notice.controller.api;

import com.example.coffies_vol_02.Notice.service.NoticeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class NoticeApiController {
    private final NoticeService noticeService;

}
