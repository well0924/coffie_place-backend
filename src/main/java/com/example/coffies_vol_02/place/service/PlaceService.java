package com.example.coffies_vol_02.place.service;

import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.constant.SearchType;
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
import java.util.ArrayList;
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

    /**
     * 가게 목록(무한 슬라이드)
     *
     * @param keyword    가게검색어
     * @param pageable   페이징 객체
     * @param member     로그인 인증에 필요한 객체
     * @return Slice<PlaceResponseDto>
     * @see RedisService#setValues(String, String) 가게 검색어 저장
     * @see PlaceRepository#placeList(Pageable, String) 가게 목록
     **/
    @Transactional
    public Slice<PlaceResponseDto> placeSlideList(Pageable pageable,String keyword, Member member) {
        if (member != null) {
            //로그인이 되었을 경우 가게이름 검색어 저장
            redisService.setValues(member.getId().toString(), keyword);
            log.info(redisService.getSearchList(member.getId().toString()));
        }
        return placeRepository.placeList(pageable, keyword);
    }

    /**
     * 가게 검색어 저장목록
     * @param member 회원 객체
     * @return getSearchList
     **/
    public List<String> placeSearchList(Member member) {
        if (member != null) {
            return redisService.getSearchList(member.getId().toString());
        }
        return null;
    }

    /**
     * 가게 검색 
     * @param keyword 검색 키워드
     * @param pageable 페이징 객체
     * @param member 로그인을 위한 객체
     **/
    @Transactional(readOnly = true)
    public Page<PlaceResponseDto> placeListAll(SearchType searchType, String keyword, Pageable pageable, Member member) {
        if (member != null) {
            //검색어 저장
            redisService.setValues(member.getId().toString(), keyword);
            log.info(redisService.getSearchList(member.getId().toString()));
        }else if(keyword == null){//키워드가 없는 경우
            throw new CustomExceptionHandler(ERRORCODE.NOT_SEARCH_VALUE);
        }
        return placeRepository.placeListSearch(searchType ,keyword, pageable);
    }

    /**
     * 가게 top5 (평점이 높은 순)
     * @param pageable 페이징 객체
     * @return Page<PlaceResponseDto>
     * @author 양경빈
     * @see PlaceRepository#placeTop5(Pageable) 가게 top5 메서드
     **/
    @Transactional(readOnly = true)
    public Page<PlaceResponseDto> placeTop5(Pageable pageable) {
        return placeRepository.placeTop5(pageable);
    }

    /**
     * 가까운 가게 5곳
     * @param lat 경도 
     * @param lon 위도
     * @return result 가게 정보 dto 값
     **/
    @Transactional(readOnly = true)
    public List<PlaceResponseDto> placeNear(Double lat, Double lon) {
        List<Place>list = placeRepository.findPlaceByLatLng(lat,lon);
        return list.stream().map(place->new PlaceResponseDto()).toList();
    }

    /**
     * 가게 단일 조회
     *
     * @param placeId 가게 번호 가게번호가 없는 경우에는 PLACE_NOT_FOUND 발생
     * @return PlaceResponseDto
     * @throws CustomExceptionHandler 가게 조회시 가게번호가 없는 경우
     * @see PlaceRepository#findById(Object) 가게 번호로 가게를 단일 조회하는 메서드
     **/
    @Transactional(readOnly = true)
    public PlaceResponseDto placeDetail(Integer placeId) {

        Place detail = placeRepository
                .findById(placeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND));;

        return PlaceResponseDto
                .builder()
                .id(detail.getId())
                .placeLat(detail.getPlaceLat())
                .placeLng(detail.getPlaceLng())
                .placeAuthor(detail.getPlaceAuthor())
                .placePhone(detail.getPlacePhone())
                .placeStart(detail.getPlaceStart())
                .placeName(detail.getPlaceName())
                .placeClose(detail.getPlaceClose())
                .placeAddr1(detail.getPlaceAddr1())
                .placeAddr2(detail.getPlaceAddr2())
                .fileGroupId(detail.getFileGroupId())
                .reviewRate(detail.getReviewRate())
                .isTitle(detail.getPlaceImageList().size() == 0 ? null : detail.getPlaceImageList().get(0).getIsTitle())
                .thumbFileImagePath(detail.getPlaceImageList().size() == 0 ? null :  detail.getPlaceImageList().get(0).getThumbFileImagePath())
                .build();
    }

    /**
     * 가게 등록
     *
     * @param dto 가게등록에 필요한 dto
     * @param imageRequestDto 이미지 등록에 필요한 dto
     * @return placeId 가게 번호
     **/
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
        //가게 이미지 업로드
        List<PlaceImage> imageList = fileHandler.placeImagesUpload(imageRequestDto.getImages());

        PlaceImage placeImage;
        //가게 이미지가 없는 경우에는 단순 가게등록
        if (imageList.isEmpty()) return registerResult;

        for (int i = 0; i < imageList.size(); i++) {
            placeImage = getPlaceImage(place, imageList, i);

            log.info("" + placeImage);
            log.info(imageList);

            place.addPlaceImage(placeImageRepository.save(placeImage));
        }
        return registerResult;
    }

    /**
     * 가게수정
     *
     * @param placeId  가게 번호
     * @param dto      가게 수정에 필요한 dto
     * @param imageDto 가게 이미지에 필요한 dto
     * @return PlaceId 가게번호
     * @throws CustomExceptionHandler PLACE_NOT_FOUND 가게가 없습니다.
     **/
    @Transactional
    public Integer placeModify(Integer placeId, PlaceRequestDto dto, PlaceImageRequestDto imageDto) throws Exception {
        Optional<Place> placeDetail = Optional.ofNullable(placeRepository
                .findById(placeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)));
        Place place = placeDetail.orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND));
        //가게 수정
        place.placeUpadate(dto);

        Integer result = place.getId();
        //가게 이미지 업로드
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
                //디비에 저장된 이미지를 삭제
                placeImageService.deletePlaceImage(placeId);
            }
            //이미지 재업로드
            imageList = fileHandler.placeImagesUpload(imageDto.getImages());

            for (int i = 0; i < imageList.size(); i++) {
                placeImage = getPlaceImage(place, imageList, i);

                place.addPlaceImage(placeImageRepository.save(placeImage));
            }
        } else {
            //이미지를 추가하지 않은채로 수정을 하는 경우
            imageList = fileHandler.placeImagesUpload(imageDto.getImages());

            for (int i = 0; i < imageList.size(); i++) {
                placeImage = getPlaceImage(place, imageList, i);

                place.addPlaceImage(placeImageRepository.save(placeImage));
            }
        }
        return result;
    }

    /**
     * 가게 삭제
     *
     * @param placeId 가게 번호 없는 경우에는 PLACE_NOT_FOUND 발생
     * @author 양경빈
     * @see PlaceRepository#findById(Object) 가게 번호로 가게를 단일조회하는 메서드
     * @see PlaceImageRepository#findPlaceImagePlace(Integer) 가게 번호로 해당 이미지 목록을 조회하는 메서드
     * @see PlaceImageService#deletePlaceImage(Integer) 가게 이미지를 삭제하는 메서드
     * @see PlaceRepository#deleteById(Object) 가게번호로 가게를 삭제하는 메서드
     **/
    public void placeDelete(Integer placeId) throws Exception {
        Optional<Place> detail = Optional.ofNullable(placeRepository.findById(placeId)
                .orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)));
        //가게이미지 목록
        List<PlaceImage> imageList = placeImageRepository.findPlaceImagePlace(placeId);

        for (PlaceImage placeImage : imageList) {
            String imgPath = placeImage.getImgPath();
            String thumbPath = placeImage.getThumbFilePath();

            File filePaths = new File(imgPath);
            File thumbPaths = new File(thumbPath);
            //가게 이미지와 섬네일을 삭제
            if (filePaths.exists()) {
                filePaths.delete();
            }
            if (thumbPaths.exists()) {
                thumbPaths.delete();
            }
            //디비에서 저장된 값 삭제
            placeImageService.deletePlaceImage(placeId);
        }
        //가게 삭제
        placeRepository.deleteById(placeId);
    }

    //엑셀목록으로 가게를 출력
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

    //가게 엑셀 목록 출력
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
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + ".xlsx");
            //파일명은 URLEncoder로 감싸주는게 좋다!

            workbook.write(response.getOutputStream());
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //가게 이미지 리사이징
    private PlaceImage getPlaceImage(Place place, List<PlaceImage> imageList, int i) {
        PlaceImage placeImage;
        String resize;
        placeImage = imageList.get(i);

        if (i == 0) {
            placeImage.setIsTitle("1");
            resize = fileHandler.ResizeImage(placeImage, 360, 360);
        } else {
            resize = fileHandler.ResizeImage(placeImage, 120, 120);
        }

        placeImage.setPlace(place);
        placeImage.setImgGroup("coffieplace");
        placeImage.setFileType("images");
        placeImage.setThumbFileImagePath(resize);
        return placeImage;
    }
}
