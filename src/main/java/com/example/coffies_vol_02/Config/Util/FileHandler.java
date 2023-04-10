package com.example.coffies_vol_02.Config.Util;

import com.example.coffies_vol_02.Attach.domain.Attach;
import com.example.coffies_vol_02.Attach.domain.AttachDto;
import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Place.domain.Place;
import com.example.coffies_vol_02.Place.domain.PlaceImage;
import com.example.coffies_vol_02.Place.domain.dto.PlaceImageDto;
import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.MultiStepRescaleOp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
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

    //게시판 파일 업로드
    public List<Attach> parseFileInfo(List<MultipartFile>multipartFiles)throws Exception{
        //반환을 할 배열
        List<Attach>list = new ArrayList<>();

        //파일이 있는 경우
        if(!CollectionUtils.isEmpty(multipartFiles)){

            // 프로젝트 디렉터리 내의 저장을 위한 절대 경로 설정
            // 경로 구분자 File.separator 사용
            String absolutePath = new File(filePath).getAbsolutePath() + File.separator + File.separator;

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

                    // 파일 권한 설정(쓰기, 읽기)
                    file.setWritable(true);
                    file.setReadable(true);
                }
            }
        }
        return list;
    }
    //가게 이미지 업로드
    public List<PlaceImage>placeImagesUpload(PlaceImageDto.PlaceImageRequestDto dto, List<MultipartFile> images)throws Exception{
        List<PlaceImage>list = new ArrayList<>();

        if(!CollectionUtils.isEmpty(images)){

            for(MultipartFile multipartFile: images){
                if(!multipartFile.isEmpty()){

                    String originalFileExtension;
                    String originFileName = multipartFile.getOriginalFilename();
                    String contentType = multipartFile.getContentType();
                    String ext = originFileName.substring(originFileName.lastIndexOf(".")+1);

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

                    String fileName = "file_"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))+"."+originalFileExtension;
                    String thumbFileName = "file_"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))+"thumb."+originalFileExtension;
                    String localPath = imgPath+dto.getImgGroup()+"/"+dto.getFileType()+"/"+thumbFileName;
                    String fullPath = new File(filePath).getAbsolutePath()+dto.getImgGroup()+File.separator+File.separator+dto.getFileType()+File.separator+File.separator+fileName;

                    if(originFileName!=null && originFileName.trim().length()>0){
                        File newFile =  new File(fullPath);

                        if(!newFile.exists()){
                            if (newFile.getParentFile().mkdirs()) {
                                newFile.createNewFile();
                            }
                        }
                        multipartFile.transferTo(newFile);

                        String path = filePath +dto.getImgGroup()+"\\"+dto.getFileType()+"\\thumb\\"+thumbFileName;

                        PlaceImageDto.PlaceImageResponseDto ResponseDto = PlaceImageDto.PlaceImageResponseDto
                                .builder()
                                .fileGroupId(dto.getFileGroupId())
                                .fileType(dto.getFileType())
                                .imgPath(fullPath)
                                .storedName(fileName)
                                .originName(originFileName)
                                .thumbFileImagePath(localPath)
                                .thumbFilePath(path)
                                .isTitle(dto.getIsTitle())
                                .imgUploader(dto.getImgUploader())
                                .build();

                        PlaceImage placeImage = new PlaceImage
                                (
                                  ResponseDto.getFileGroupId(),
                                  ResponseDto.getFileType(),
                                  ResponseDto.getImgPath(),
                                  ResponseDto.getStoredName(),
                                  ResponseDto.getOriginName(),
                                  ResponseDto.getThumbFileImagePath(),
                                  ResponseDto.getThumbFilePath(),
                                  ResponseDto.getImgPath(),
                                  ResponseDto.getImgGroup(),
                                  ResponseDto.getIsTitle()
                                );

                        list.add(placeImage);
                    }
                }
            }
        }
        return list;
    }
    //가게 이미지 리사이징
    public String ResizeImage(PlaceImage dto,int width,int height){
        String defaultFolder = filePath+File.separator+dto.getImgGroup()+File.separator+dto.getFileType()+File.separator;

        String originFilePath = defaultFolder+dto.getStoredName();

        File file = new File(originFilePath);

        String thumblocalPath = "";

        boolean resultCode = false;

        try {

            if(filePath != null && filePath.length() !=0) {

                String originFileName = file.getName();

                String ext = originFileName.substring(originFileName.lastIndexOf(".")+1);

                String thumbFileName = originFileName.substring(0,originFileName.lastIndexOf("."))+"_thumb."+ext;

                BufferedImage originImage = ImageIO.read(new FileInputStream(file));

                MultiStepRescaleOp scaleImage = new MultiStepRescaleOp(width,height);

                scaleImage.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Soft);

                BufferedImage resizeImage = scaleImage.filter(originImage,null);

                String fullPath = defaultFolder + "thumb"+File.separator+ thumbFileName;

                File out = new File(fullPath);

                if(!out.getParentFile().exists()) {
                    out.getParentFile().mkdirs();
                }

                if(!out.exists()) {
                    resultCode = ImageIO.write(resizeImage, ext, out);
                    if(resultCode) {
                        thumblocalPath = imgPath + dto.getImgGroup()+"/"+dto.getFileType()+"/thumb/"+out.getName();
                    }else {
                        thumblocalPath = null;

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return thumblocalPath;
    }
}
