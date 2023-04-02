package com.example.coffies_vol_02.Attach.service;

import com.example.coffies_vol_02.Attach.domain.Attach;
import com.example.coffies_vol_02.Attach.domain.AttachDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileHandler {
    @Value("${server.file.upload}")
    private String filePath;
    @Value("/istatic/images/")
    private String imgPath;

    //파일 업로드
    public List<Attach> parseFileInfo(List<MultipartFile>multipartFiles)throws Exception{
        //반환을 할 배열
        List<Attach>list = new ArrayList<>();

        //파일이 있는 경우
        if(!CollectionUtils.isEmpty(multipartFiles)){

            LocalDateTime now = LocalDateTime.now();

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

            String current_date = now.format(dateTimeFormatter);

            // 프로젝트 디렉터리 내의 저장을 위한 절대 경로 설정
            // 경로 구분자 File.separator 사용
            String absolutePath = new File(filePath).getAbsolutePath() + File.separator + File.separator;

            // 파일을 저장할 세부 경로 지정
            //String path = "images" + File.separator + current_date;

            File file = new File(absolutePath);

            // 디렉터리가 존재하지 않을 경우
            if(!file.exists()) {
                boolean wasSuccessful = file.mkdirs();
                System.out.println(wasSuccessful);
                // 디렉터리 생성에 실패했을 경우
                if(!wasSuccessful)
                    System.out.println("file: was not successful");
            }

            //다중 파일 처리
            for(MultipartFile multipartFile : multipartFiles){

                if(!multipartFile.isEmpty()){//파일이 있는 경우
                    // 파일의 확장자 추출
                    String originalFileExtension;
                    String originFileName = multipartFile.getOriginalFilename();
                    String contentType = multipartFile.getContentType();
                    String ext = originFileName.substring(originFileName.lastIndexOf(".")+1);

                    // 확장자명이 존재하지 않을 경우 처리 x
                    if(ObjectUtils.isEmpty(contentType)) {
                        break;
                    }else {
                        if(contentType.contains("image/jpeg"))
                            originalFileExtension = ".jpg";
                        else if(contentType.contains("image/png"))
                            originalFileExtension = ".png";
                        else
                            originalFileExtension= ext;
                    }

                    // 파일명 중복 피하고자 나노초까지 얻어와 지정
                    String new_file_name = System.nanoTime() +"."+originalFileExtension;

                    AttachDto attachDto = AttachDto
                            .builder()
                            .originFileName(originFileName)
                            .fileSize(multipartFile.getSize())
                            .filePath(filePath+File.separator+new_file_name)
                            .build();

                    Attach attachFile = new Attach(attachDto.getOriginFileName(),attachDto.getFilePath(),attachDto.getFileSize());

                    list.add(attachFile);
                    // 업로드 한 파일 데이터를 지정한 파일에 저장
                    file = new File( absolutePath + File.separator + new_file_name);
                    multipartFile.transferTo(file);
                    System.out.println(file);
                    // 파일 권한 설정(쓰기, 읽기)
                    file.setWritable(true);
                    file.setReadable(true);
                }
            }
        }
        return list;
    }
}
