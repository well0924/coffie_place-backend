package com.example.coffies_vol_02.Place.service;

import com.example.coffies_vol_02.Config.Exception.ERRORCODE;
import com.example.coffies_vol_02.Config.Exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.Config.Util.FileHandler;
import com.example.coffies_vol_02.Place.domain.Place;
import com.example.coffies_vol_02.Place.domain.PlaceImage;
import com.example.coffies_vol_02.Place.domain.dto.PlaceDto;
import com.example.coffies_vol_02.Place.domain.dto.PlaceImageDto;
import com.example.coffies_vol_02.Place.repository.PlaceImageRepository;
import com.example.coffies_vol_02.Place.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final FileHandler fileHandler;
    private final PlaceImageRepository placeImageRepository;
    private final PlaceImageService placeImageService;

    /*
    * 가게 목록
    */
    @Transactional(readOnly = true)
    public Page<PlaceDto.PlaceResponseDto>placeList(Pageable pageable){
        Page<Place>list = placeRepository.findAll(pageable);
        return list.map(place -> new PlaceDto.PlaceResponseDto(place));
    }
    
    /*
    *  가게 단일 조회
    */
    @Transactional
    public PlaceDto.PlaceResponseDto placeDetail(Integer placeId){
        Optional<Place> place = Optional.of(placeRepository.findById(placeId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_LIST)));
        Place detail = place.get();
        return PlaceDto.PlaceResponseDto
                .builder()
                .place(detail)
                .build();
    }
    
    /*
    * 가게 등록
    */
    @Transactional
    public Integer placeRegister(PlaceDto.PlaceRequestDto dto, PlaceImageDto.PlaceImageRequestDto requestDto) throws Exception {
        Place place = Place
                .builder()
                .placeLat(dto.getPlaceLat())
                .placeLng(dto.getPlaceLng())
                .placeName(dto.getPlaceName())
                .placePhone(dto.getPlacePhone())
                .placeStart(dto.getPlaceStart())
                .placeClose(dto.getPlaceClose())
                .placeAuthor(dto.getPlaceAuthor())
                .placeAddr1(dto.getPlaceAddr1())
                .placeAddr2(dto.getPlaceAddr2())
                .fileGroupId(dto.getFileGroupId())
                .build();

        int registerResult = placeRepository.save(place).getId();

        List<PlaceImage>placeImageList =new ArrayList<>();
        PlaceImage responseDto = null;

        if(registerResult>0){
            placeImageList = fileHandler.placeImagesUpload(requestDto,dto.getImages());
        }

        if(!placeImageList.isEmpty()){
            String resize = "";

            for(int i=0;i<placeImageList.size();i++){
                responseDto = placeImageList.get(i);
                if(i==0){
                    responseDto.setIsTitle("1");
                    resize = fileHandler.ResizeImage(responseDto,360,360);
                }else{
                    resize =fileHandler.ResizeImage(responseDto,120,120);
                }
                responseDto.setImgGroup("coffieplace");
                responseDto.setFileType("place");
                responseDto.setThumbFileImagePath(resize);
            }

            for(PlaceImage placeImage :placeImageList){
                place.addPlaceImage(placeImageRepository.save(placeImage));
            }
        }
        return registerResult;
    }

    /*
     * 가게 수정
     */
    @Transactional
    public Integer placeModify(Integer placeId,PlaceDto.PlaceRequestDto dto,PlaceImageDto.PlaceImageRequestDto imageDto) throws Exception {
        Optional<Place>placeDetail = Optional.ofNullable(placeRepository.findById(placeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)));
        placeDetail.get().placeUpadate(dto);

        int result = placeDetail.get().getId();
        List<PlaceImage>imageList = placeImageRepository.findPlaceImagePlace(placeId);

        if(!imageList.isEmpty()){
            for(int i=0;i< imageList.size();i++){
                String imagePath = imageList.get(i).getImgPath();
                String thumbPath = imageList.get(i).getThumbFilePath();
                File image = new File(imagePath);
                File thumb = new File(thumbPath);

                if(image.exists()){
                    image.delete();
                }
                if(thumb.exists()){
                    thumb.delete();
                }
                placeImageService.deletePlaceImage(placeId);
            }
            imageList = fileHandler.placeImagesUpload(imageDto,dto.getImages());
            for(PlaceImage placeImage : imageList){
                placeDetail.get().addPlaceImage(placeImageRepository.save(placeImage));
            }
        }else{
            imageList = fileHandler.placeImagesUpload(imageDto,dto.getImages());
            for(PlaceImage placeImage : imageList){
                placeDetail.get().addPlaceImage(placeImageRepository.save(placeImage));
            }
        }
        return result;
    }

    /*
     * 가게 삭제
     */
    public void placeDelete(Integer placeId) throws Exception {
        Optional<Place>detail = Optional.ofNullable(placeRepository.findById(placeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)));

        List<PlaceImageDto.PlaceImageResponseDto>imagelist = placeImageService.placeImageResponseDtoList(placeId);

        for(int i= 0; i<imagelist.size();i++){
            String filePath= imagelist.get(i).getImgPath();
            String thumbPath = imagelist.get(i).getThumbFilePath();

            File path = new File(filePath);
            File thumb = new File(thumbPath);

            if(path.exists()){
                path.delete();
            }
            if(thumb.exists()){
                thumb.delete();
            }
        }
        placeRepository.deleteById(placeId);
    }

    /*
    *  가게 목록(엑셀 파일)
    */
    public Object getPlaceList(HttpServletResponse response,boolean execelDown){
        List<Place>placeList = placeRepository.findAll();

        if(execelDown){
            createExcelDownload(response,placeList);
        }
        return placeList;
    }

    /*
    * 목록 엑셀 파일
    */
    private void createExcelDownload(HttpServletResponse response, List<Place> placeList){
        try{
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("등록 가게 목록");
            CellStyle numberCellStyle = workbook.createCellStyle();
            numberCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //파일명
            final String fileName = "사용자 포인트 통계";

            //헤더
            final String[] header = {"번호", "가게명","가게 등록자","가게전화번호", "가게 시작시간", "가게 종료시간","가게주소1","가게주소2"};
            Row row = sheet.createRow(0);
            for (int i = 0; i < header.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(header[i]);
            }
            //바디
            for (int i = 0; i < placeList.size(); i++) {
                row = sheet.createRow(i + 1);  //헤더 이후로 데이터가 출력되어야하니 +1

                Place place = placeList.get(i);

                Cell cell = null;
                cell = row.createCell(0);
                cell.setCellValue(place.getId());

                cell = row.createCell(1);
                cell.setCellValue(place.getPlaceName());

                cell = row.createCell(2);
                cell.setCellValue(place.getPlaceAuthor());

                cell = row.createCell(3);
                cell.setCellValue(place.getPlacePhone());

                cell = row.createCell(4);
                cell.setCellValue(place.getPlaceStart());

                cell = row.createCell(5);
                cell.setCellValue(place.getPlaceClose());

                cell = row.createCell(6);
                cell.setCellValue(place.getPlaceAddr1());

                cell = row.createCell(6);
                cell.setCellValue(place.getPlaceAddr2());
            }


            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(fileName, "UTF-8")+".xlsx");
            //파일명은 URLEncoder로 감싸주는게 좋다!

            workbook.write(response.getOutputStream());
            workbook.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
