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
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@AllArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final FileHandler fileHandler;
    private final PlaceImageRepository placeImageRepository;
    private final PlaceImageService placeImageService;
    private final ObjectMapper objectMapper;

    /*
    * 가게 목록
    */
    @Transactional(readOnly = true)
    public Page<PlaceDto.PlaceResponseDto>placeList(Pageable pageable){
        Page<Place>list = placeRepository.findAll(pageable);
        return list.map(place -> new PlaceDto.PlaceResponseDto(place));
    }
    
    /*
    * 가게 검색
    */
    @Transactional(readOnly = true)
    public Page<PlaceDto.PlaceResponseDto>placeListAll(String keyword,Pageable pageable){
        Page<PlaceDto.PlaceResponseDto>result = placeRepository.placeListSearch(keyword,pageable);
        return result;
    }

    /*
    * 가게 top5
    */
    @Transactional(readOnly = true)
    public Page<PlaceDto.PlaceResponseDto>placeTop5(Pageable pageable){
        return placeRepository.placeTop5(pageable);
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
    public Integer placeRegister(PlaceDto.PlaceRequestDto dto,PlaceImageDto.PlaceImageRequestDto imageRequestDto) throws Exception {
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
                .reviewRate(dto.getReviewRate())
                .build();

        placeRepository.save(place);

        Integer registerResult = place.getId();

        List<PlaceImage>imageList = fileHandler.placeImagesUpload(imageRequestDto.getImages());

        PlaceImage placeImage = new PlaceImage();

        if(imageList.size()==0||imageList.isEmpty()){
            return registerResult;
        }

        if(!imageList.isEmpty()) {
            String resize = "";
            for(int i=0;i< imageList.size();i++){
                placeImage= imageList.get(i);
                if(i == 0){
                    placeImage.setIsTitle("1");
                    resize = fileHandler.ResizeImage(placeImage,360,360);
                }else{
                    resize = fileHandler.ResizeImage(placeImage,120,120);
                }
                placeImage.setPlace(place);
                placeImage.setImgGroup("coffieplace");
                placeImage.setFileType("images");
                placeImage.setThumbFileImagePath(resize);

                place.addPlaceImage(placeImageRepository.save(placeImage));
            }
            /*for (PlaceImage placeImage : imageList) {
                place.addPlaceImage(placeImageRepository.save(placeImage));
                log.info("??"+placeImage);
            }*/
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

        return result;
    }

    /*
     * 가게 삭제
     */
    public void placeDelete(Integer placeId) throws Exception {
        Optional<Place>detail = Optional.ofNullable(placeRepository.findById(placeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)));

        placeRepository.deleteById(placeId);
    }

    /*
     *
     */
    public Object getPlaceList(HttpServletResponse response, boolean excelDownload) {

        List<Place> placePlace = placeRepository.findAll();
        if(excelDownload){
            createExcelDownloadResponse(response, placePlace);
            return null; //없으면 에러!
        }
        List<Map> placeList = placePlace.stream()
                .map(place -> objectMapper.convertValue(place, Map.class))
                .collect(Collectors.toList());
        return placeList;
    }

    /*
     * 가게 목록 엑셀 다운로드
     */
    private void createExcelDownloadResponse(HttpServletResponse response, List<Place>placeList) {

        try{
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("등록 가게목록");

            //파일명
            final String fileName = "등록 가게 목록";

            //헤더
            final String[] header = {"번호","가게 이름","등록자","가게전화번호","시작시간", "종료시간","가게 주소1","가게 주소2","가게 평점","파일 번호","가게 위도","가게 경도"};

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

                cell = row.createCell(7);
                cell.setCellValue(place.getPlaceAddr2());

                cell = row.createCell(8);
                cell.setCellValue(place.getReviewRate());

                cell = row.createCell(9);
                cell.setCellValue(place.getFileGroupId());

                cell = row.createCell(10);
                cell.setCellValue(place.getPlaceLng());

                cell = row.createCell(11);
                cell.setCellValue(place.getPlaceLat());
            }


            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(fileName, "UTF-8")+".xlsx");
            //파일명은 URLEncoder로 감싸주는게 좋다!

            workbook.write(response.getOutputStream());
            workbook.close();

        }catch(IOException e){
            e.printStackTrace();
        }

    }
}
