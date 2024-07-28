package com.example.coffies_vol_02.place.service;

import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.config.redis.CacheKey;
import com.example.coffies_vol_02.config.util.FileHandler;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceImageRequestDto;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceRequestDto;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import com.example.coffies_vol_02.place.repository.PlaceImageRepository;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@Transactional
@AllArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    private final FileHandler fileHandler;

    private final PlaceImageService placeImageService;

    private final PlaceImageRepository placeImageRepository;

    /**
     * 가게 목록(무한 슬라이드)
     *
     * @param keyword    가게검색어
     * @param pageable   페이징 객체
     * @param member     로그인 인증에 필요한 객체
     * @return Slice<PlaceResponseDto>
     * @see PlaceRepository#placeList(Pageable, String) 가게 목록
     **/
    public Slice<PlaceResponseDto> listCafePlace(Pageable pageable,String keyword, Member member) {
        return placeRepository.placeList(pageable, keyword);
    }

    /**
     * 가게 검색 
     * @param keyword 검색 키워드
     * @param pageable 페이징 객체
     * @param member 로그인을 위한 객체
     * @throws CustomExceptionHandler 가게 검색시 검색어가 없는 경우
     **/
    @Transactional(readOnly = true)
    public Slice<PlaceResponseDto> searchCafePlace(SearchType searchType, String keyword, Pageable pageable, Member member) {
        if(keyword == null||searchType == null){//키워드가 없는 경우
            throw new CustomExceptionHandler(ERRORCODE.NOT_SEARCH_VALUE);
        }
        return placeRepository.placeListSearch(searchType,keyword, pageable);
    }

    /**
     * 가게 top5 (평점이 높은 순)
     * @param pageable 페이징 객체
     * @return Page<PlaceResponseDto>
     * @author 양경빈
     * @see PlaceRepository#placeTop5(Pageable) 가게 top5 메서드
     **/
    /*@Transactional(readOnly = true)
    public Page<PlaceResponseDto> cafePlaceByReviewRateTop5(Pageable pageable) {
        return placeRepository.placeTop5(pageable);
    }*/

    /**
     * 가게 단일 조회
     * @param placeId 가게 번호 가게번호가 없는 경우에는 PLACE_NOT_FOUND 발생
     * @return PlaceResponseDto
     * @throws CustomExceptionHandler 가게 조회시 가게번호가 없는 경우
     * @see PlaceRepository#findById(Object) 가게 번호로 가게를 단일 조회하는 메서드
     **/
    @Cacheable(value = CacheKey.PLACE,key = "#placeId")
    public PlaceResponseDto findCafePlaceById(Integer placeId) {

        Place detail = placeRepository.findById(placeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND));

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
                .isTitle(detail.getPlaceImageList().isEmpty() ? null : detail.getPlaceImageList().get(0).getIsTitle())
                .thumbFileImagePath(detail.getPlaceImageList().isEmpty() ? null :  detail.getPlaceImageList().get(0).getThumbFileImagePath())
                .build();
    }

    /**
     * 가게 등록
     *
     * @param dto 가게등록에 필요한 dto
     * @param imageRequestDto 이미지 등록에 필요한 dto
     * @see FileHandler#placeImagesUpload(List) 가게 이미지를 등록하는 메서드                       
     * @return placeId 가게 번호
     **/
    @Transactional
    public Integer createCafePlace(PlaceRequestDto dto, PlaceImageRequestDto imageRequestDto) throws Exception {
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
     * @see PlaceRepository#findById(Object) 가게번호로 가게를 조회하는 메서드
     * @see FileHandler#placeImagesUpload(List) 가게 수정시 이미지를 업로드하는 메서드
     * @return PlaceId 가게번호
     * @throws CustomExceptionHandler PLACE_NOT_FOUND 가게가 없습니다.
     **/
    public Integer updateCafePlace(Integer placeId, PlaceRequestDto dto, PlaceImageRequestDto imageDto) throws Exception {
        Optional<Place> placeDetail = Optional.ofNullable(placeRepository
                .findById(placeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)));

        Place place = placeDetail.orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND));
        //가게 수정
        place.placeUpdate(dto);

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
    public void deleteCafePlace(Integer placeId) throws Exception {
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
