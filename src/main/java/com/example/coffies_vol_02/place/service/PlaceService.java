package com.example.coffies_vol_02.place.service;

import com.example.coffies_vol_02.config.exception.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.config.redis.RedisService;
import com.example.coffies_vol_02.config.util.FileHandler;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceImageRequestDto;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceRequestDto;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import com.example.coffies_vol_02.place.repository.PlaceImageRepository;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private final PlaceImageService placeImageService;
    private final PlaceImageRepository placeImageRepository;
    private final ObjectMapper objectMapper;
    private final RedisService redisService;

    @Transactional(readOnly = true)
    public Page<PlaceResponseDto>placeSlideList(Pageable pageable){
        Page<Place>list = placeRepository.findAll(pageable);
        return list.map(PlaceResponseDto::new);
    }

    public Slice<PlaceResponseDto> placeSlideList(Pageable pageable, String keyword, String searchType, Member member) {
        if (member != null && searchType != null) {
            redisService.setValues(member.getId().toString(), keyword);
            System.out.println(redisService.getSearchList(member.getId().toString()));
        }
        return placeRepository.placeList(pageable, keyword);
    }

    public List<String> placeSearchList(Member member) {
        if (member != null) {
            return redisService.getSearchList(member.getId().toString());
        }
        return null;
    }

    @Transactional(readOnly = true)
    public Page<PlaceResponseDto> placeListAll(String keyword, Pageable pageable, Member member) {
        if (member != null) {
            redisService.setValues(member.getId().toString(), keyword);
            System.out.println(redisService.getSearchList(member.getId().toString()));
        }
        return placeRepository.placeListSearch(keyword, pageable);
    }

    @Transactional(readOnly = true)
    public Page<PlaceResponseDto> placeTop5(Pageable pageable) {
        return placeRepository.placeTop5(pageable);
    }

    @Transactional
    public PlaceResponseDto placeDetail(Integer placeId) {
        Optional<Place> place = Optional.of(placeRepository.findById(placeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_LIST)));
        Place detail = place.orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND));
        return PlaceResponseDto
                .builder()
                .id(detail.getId())
                .placeLat(detail.getPlaceLat())
                .placeLng(detail.getPlaceLng())
                .placeAuthor(detail.getPlaceAuthor())
                .placePhone(detail.getPlacePhone())
                .placeStart(detail.getPlaceStart())
                .placeClose(detail.getPlaceClose())
                .placeAddr1(detail.getPlaceAddr1())
                .placeAddr2(detail.getPlaceAddr2())
                .fileGroupId(detail.getFileGroupId())
                .reviewRate(detail.getReviewRate())
                .isTitle(detail.getPlaceImageList().get(0).getIsTitle())
                .thumbFileImagePath(detail.getPlaceImageList().get(0).getThumbFileImagePath())
                .build();
    }

    @Transactional
    public Integer placeRegister(PlaceRequestDto dto, PlaceImageRequestDto imageRequestDto) throws Exception {
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

        PlaceImage placeImage;

        if(imageList.isEmpty()) return registerResult;

        for (int i = 0; i < imageList.size(); i++) {
            placeImage = getPlaceImage(place, imageList, i);

            log.info("" + placeImage);
            log.info(imageList);

            place.addPlaceImage(placeImageRepository.save(placeImage));
        }
        return registerResult;
    }

    @Transactional
    public Integer placeModify(Integer placeId, PlaceRequestDto dto, PlaceImageRequestDto imageDto) throws Exception {
        Optional<Place> placeDetail = Optional.ofNullable(placeRepository.findById(placeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)));
        Place place = placeDetail.orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND));

        place.placeUpadate(dto);

        Integer result = place.getId();

        List<PlaceImage> imageList = placeImageRepository.findPlaceImagePlace(placeId);

        PlaceImage placeImage;

        if (result > 0) {
            //이미지가 없는 경우
            if (imageList.isEmpty()) return result;

            //이미지가 있는 경우
            for (PlaceImage image : imageList) {

                String filePath = image.getImgPath();
                String thumbPath = image.getThumbFilePath();

                File filePaths = new File(filePath);
                File thumbPaths = new File(thumbPath);

                if (filePaths.exists()) filePaths.delete();
                if (thumbPaths.exists()) thumbPaths.delete();
                //디베
                placeImageService.deletePlaceImage(placeId);
            }

            imageList = fileHandler.placeImagesUpload(imageDto.getImages());

            for(int i=0;i< imageList.size();i++){
                placeImage = getPlaceImage(place, imageList, i);

                place.addPlaceImage(placeImageRepository.save(placeImage));
            }
        } else {
            //이미지를 추가하지 않은채로 수정을 하는 경우
            imageList = fileHandler.placeImagesUpload(imageDto.getImages());

            for(int i=0;i< imageList.size();i++){
                placeImage = getPlaceImage(place, imageList, i);

                place.addPlaceImage(placeImageRepository.save(placeImage));
            }
        }
        return result;
    }

    public void placeDelete(Integer placeId) throws Exception {
        Optional<Place>detail = Optional.ofNullable(placeRepository.findById(placeId)
                .orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)));

        List<PlaceImage>imageList = placeImageRepository.findPlaceImagePlace(placeId);

        for(PlaceImage placeImage : imageList){
            String imgPath = placeImage.getImgPath();
            String thumbPath = placeImage.getThumbFilePath();

            File filePaths = new File(imgPath);
            File thumbPaths = new File(thumbPath);

            if (filePaths.exists()) {
                filePaths.delete();
            }
            if (thumbPaths.exists()) {
                thumbPaths.delete();
            }
            //디비에서 저장된 값 삭제
            placeImageService.deletePlaceImage(placeId); 
        }
        placeRepository.deleteById(placeId);
    }

    public Object getPlaceList(HttpServletResponse response, boolean excelDownload) {

        List<Place> placePlace = placeRepository.findAll();

        if (excelDownload) {
            createExcelDownloadResponse(response, placePlace);
            return null; //없으면 에러!
        }

        return placePlace
                .stream()
                .map(place -> objectMapper.convertValue(place, Map.class))
                .collect(Collectors.toList());
    }

    private void createExcelDownloadResponse(HttpServletResponse response, List<Place> placeList) {

        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("등록 가게목록");

            //파일명
            final String fileName = "등록 가게 목록";

            //헤더
            final String[] header = {"번호", "가게 이름", "등록자", "가게전화번호", "시작시간", "종료시간", "가게 주소1", "가게 주소2", "가게 평점", "파일 번호", "가게 위도", "가게 경도"};

            Row row = sheet.createRow(0);

            for (int i = 0; i < header.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(header[i]);
            }

            //바디
            for (int i = 0; i < placeList.size(); i++) {
                row = sheet.createRow(i + 1);  //헤더 이후로 데이터가 출력되어야하니 +1

                Place place = placeList.get(i);

                Cell cell;
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
            response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(fileName, StandardCharsets.UTF_8)+".xlsx");
            //파일명은 URLEncoder로 감싸주는게 좋다!

            workbook.write(response.getOutputStream());
            workbook.close();

        }catch(IOException e){
            e.printStackTrace();
        }

    }
    
    private PlaceImage getPlaceImage(Place place, List<PlaceImage> imageList, int i) {
        PlaceImage placeImage;
        String resize;
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
        return placeImage;
    }
}
