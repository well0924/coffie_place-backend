package com.example.coffies_vol_02.config.util;

import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.example.coffies_vol_02.place.domain.dto.PlaceImageDto;
import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.MultiStepRescaleOp;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
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
                System.out.println("file create");
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
    public List<PlaceImage>placeImagesUpload(List<MultipartFile>images)throws Exception{
        List<PlaceImage>list = new ArrayList<>();
        //업로드할 이미지가 있는경우
        if(!CollectionUtils.isEmpty(images)){
            //이미지 다중 처리
            for(MultipartFile multipartFile : images){

                if(!multipartFile.isEmpty()){//파일이 있는 경우
                    // 파일의 확장자 추출
                    String originalFileExtension;

                    String contentType = multipartFile.getContentType();

                    String originFileName = multipartFile.getOriginalFilename();

                    String ext = originFileName.substring(originFileName.lastIndexOf(".")+1);

                    String uuid = UUID.randomUUID().toString();

                    String fileGroupId = "place_"+uuid.substring(0,uuid.indexOf("-"));

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
                    String new_file_name = System.nanoTime()+originalFileExtension;

                    String thumbFileName = "file_"+System.nanoTime()+"_thumb."+ext;

                    String fullPath = filePath+"coffieplace"+"\\"+"images"+"\\"+new_file_name;
                    String localPath = imgPath+"coffieplace"+"/"+"images"+"/"+thumbFileName;
                    String path = filePath+"coffieplace"+"\\"+"images"+"\\thumb\\"+thumbFileName;
                    File file = new File(fullPath);

                    // 디렉터리가 존재하지 않을 경우
                    if(!file.exists()) {
                        if(file.getParentFile().mkdirs()){
                            boolean IsSuccess =file.createNewFile();
                            log.info(IsSuccess);
                        }
                    }

                    PlaceImageDto.PlaceImageRequestDto placeImageRequestDto = PlaceImageDto.PlaceImageRequestDto
                            .builder()
                            .fileGroupId(fileGroupId)
                            .fileType("images")
                            .imgGroup("coffieplace")
                            .imgPath(fullPath)
                            .thumbFileImagePath(localPath)
                            .thumbFilePath(path)
                            .storedName(new_file_name)
                            .originName(originFileName)
                            .imgUploader("well4149")
                            .isTitle("N")
                            .build();

                    PlaceImage placeImage = new PlaceImage(
                            placeImageRequestDto.getFileGroupId(),
                            placeImageRequestDto.getFileType(),
                            placeImageRequestDto.getImgGroup(),
                            placeImageRequestDto.getImgPath(),
                            placeImageRequestDto.getThumbFileImagePath(),
                            placeImageRequestDto.getThumbFilePath(),
                            placeImageRequestDto.getOriginName(),
                            placeImageRequestDto.getStoredName(),
                            placeImageRequestDto.getImgUploader(),
                            placeImageRequestDto.getIsTitle());

                    list.add(placeImage);
                    // 업로드 한 파일 데이터를 지정한 파일에 저장
                    file = new File(fullPath);
                    multipartFile.transferTo(file);
                    // 파일 권한 설정(쓰기, 읽기)
                    file.setWritable(true);
                    file.setReadable(true);
                }
            }
        }
        return list;
    }

    //가게 이미지 리사이징
    public String ResizeImage(PlaceImage dto,int width,int height){

        String defaultFolder = filePath+dto.getImgGroup()+File.separator+File.separator+dto.getFileType();

        String originFilePath = defaultFolder+"\\"+dto.getOriginName();

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

                String fullPath = defaultFolder+File.separator+File.separator + "thumb"+File.separator+File.separator+ thumbFileName;

                File out = new File(fullPath);

                log.info(out);

                if(!out.getParentFile().exists()) {
                    boolean filecreate=out.getParentFile().mkdirs();
                    log.info(filecreate);
                }

                if(!out.exists()) {
                    resultCode = ImageIO.write(resizeImage, ext, out);
                    log.info(resultCode);
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
