package com.example.coffies_vol_02.Attach.controller;

import com.example.coffies_vol_02.Attach.service.AttachService;
import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/file")
public class FileApiController {

    private final AttachService attachService;

    @GetMapping("/filelist")
    public CommonResponse<?>fileList(){
        return new CommonResponse<>();
    }

    @DeleteMapping("/delete")
    public CommonResponse<?>fileDelete(){
        return new CommonResponse<>();
    }

    @GetMapping("/download")
    public CommonResponse<?>fileDownload(){
        return new CommonResponse<>();
    }
}
