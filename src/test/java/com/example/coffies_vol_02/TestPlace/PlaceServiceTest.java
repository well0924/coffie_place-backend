package com.example.coffies_vol_02.TestPlace;

import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.repository.AttachRepository;
import com.example.coffies_vol_02.attach.service.AttachService;
import com.example.coffies_vol_02.config.constant.Role;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.redis.RedisService;
import com.example.coffies_vol_02.config.util.FileHandler;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceImageRequestDto;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceRequestDto;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceImageResponseDto;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import com.example.coffies_vol_02.place.repository.PlaceImageRepository;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import com.example.coffies_vol_02.place.service.PlaceImageService;
import com.example.coffies_vol_02.place.service.PlaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PlaceServiceTest {
    @InjectMocks
    private PlaceService placeService;

    @Mock
    private PlaceImageService placeImageService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private RedisService redisService;

    @Mock
    private PlaceImageRepository placeImageRepository;

    @Mock
    private AttachRepository attachRepository;

    @Mock
    private AttachService attachService;

    @Mock
    private FileHandler fileHandler;

    private Member member;

    private Place place;

    private PlaceImage placeImage;

    private Attach attach;

    private PlaceRequestDto placeRequestDto;

    private PlaceResponseDto placeResponseDto;

    private PlaceImageRequestDto placeImageRequestDto;
    private PlaceImageResponseDto placeImageResponseDto;

    List<Place>placeList = new ArrayList<>();

    List<PlaceImage> placeImages = new ArrayList<>();

    List<PlaceImageResponseDto> detailefileList = new ArrayList<>();

    List<PlaceImage>filelist = new ArrayList<>();

    List<MultipartFile>files = new ArrayList<>(List.of(
            new MockMultipartFile("test1", "가게 이미지1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
            new MockMultipartFile("test2", "가게 이미지2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()),
            new MockMultipartFile("test3", "가게 이미지3.PNG", MediaType.IMAGE_PNG_VALUE, "test3".getBytes())));

    @BeforeEach
    public void init() throws Exception {
        member = memberDto();
        placeImage = placeImage();
        place = place();
        place.addPlaceImage(placeImage);
        placeList.add(place);
        placeImages.add(placeImage());
        filelist.add(placeImage);
        fileHandler.placeImagesUpload(files);
        placeRequestDto = placeRequestDto();
        placeResponseDto = placeResponseDto();
        placeImageRequestDto = placeImageRequestDto();
        placeImageResponseDto = placeImageResponseDto();
        detailefileList.add(placeImageResponseDto);
        detailefileList = placeImageService.placeImageResponseDtoList(place.getId());
    }

    @Test
    @DisplayName("가게 목록-성공")
    public void placeListTest(){
        List<String>listName = new ArrayList<>();
        listName.add(place.getPlaceName());

        List<PlaceResponseDto>placelist = new ArrayList<>();
        placelist.add(placeResponseDto);
        PageRequest pageRequest =  PageRequest.of(0,5, Sort.by("id").descending());

        Slice<PlaceResponseDto>sliceDto = new SliceImpl<>(placelist,pageRequest,true);

        //given
        given(memberRepository.findByUserId(member.getUserId())).willReturn(Optional.of(member));
        given(placeRepository.placeList(pageRequest,place.getPlaceName())).willReturn(sliceDto);
        given(redisService.getSearchList(place.getPlaceName())).willReturn(listName);
        //when
        when(placeService.placeSlideList(pageRequest,place.getPlaceName(),member)).thenReturn(sliceDto);
        //then
        assertThat(placelist.size()).isNotNull();
    }

    @Test
    @DisplayName("가게 조회-성공")
    public void placeDetailTest(){
        //given
        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        //when
        placeResponseDto = placeService.placeDetail(place.getId());
        //then
        assertThat(placeResponseDto).isNotNull();
        assertThat(placeResponseDto.getPlaceName()).isEqualTo(place.getPlaceName());
        assertThat(placeResponseDto.getPlaceAuthor()).isEqualTo(place.getPlaceAuthor());
    }

    @Test
    @DisplayName("가게 검색- 성공")
    public void placeSearchTest(){
        String keyword = place.getPlaceName();

        PageRequest pageRequest = PageRequest.of(0,1,Sort.by("id").descending());

        List<PlaceResponseDto>list = new ArrayList<>();
        list.add(placeResponseDto);

        Page<PlaceResponseDto>placeList = new PageImpl<>(list,pageRequest,0);

        given(placeRepository.placeListSearch(SearchType.p,keyword,pageRequest)).willReturn(placeList);

        when(placeService.placeListAll(SearchType.p,keyword,pageRequest,member)).thenReturn(placeList);
        placeList = placeService.placeListAll(SearchType.p,keyword,pageRequest,member);

        assertThat(placeList.toList()).isNotEmpty();
    }

    @Test
    @DisplayName("가게 근처 조회")
    public void nearPlaceTest(){
        List<PlaceResponseDto>placeResponseDtos = new ArrayList<>();
        placeResponseDtos.add(placeResponseDto);

        //given
        given(placeRepository.findPlaceByLatLng(member.getMemberLat(),member.getMemberLng())).willReturn(placeList);

        //when
        when(placeService.placeNear(member.getMemberLat(),member.getMemberLng())).thenReturn(placeResponseDtos);

        //then
        System.out.println(placeResponseDtos);
        assertThat(placeResponseDtos.size()).isNotNull();
        assertThat(placeResponseDtos);
    }

    @Test
    @DisplayName("가게 등록")
    public void placeRegisterTest()throws Exception{
        //given
        given(placeRepository.save(place)).willReturn(place);
        given(placeImageRepository.save(placeImage)).willReturn(placeImage);
        given(fileHandler.placeImagesUpload(files)).willReturn(placeImages);
        given(fileHandler.ResizeImage(placeImage,240,240)).willReturn("");
        //when

        //then

    }

    @Test
    @DisplayName("가게 수정")
    public void placeModifyTest(){
        //given

        //when

        //then

    }

    @Test
    @DisplayName("가게 삭제")
    public void placeDeleteTest()throws Exception{
        //given
        given(placeRepository.save(any())).willReturn(place);
        given(fileHandler.placeImagesUpload(files)).willReturn(filelist);
        //when

        //then

    }

    private Member memberDto(){
        return Member
                .builder()
                .id(1)
                .userId("well4149")
                .password("qwer4149!!")
                .memberName("userName")
                .userEmail("well414965@gmail.com")
                .userPhone("010-9999-9999")
                .userAge("20")
                .userGender("남자")
                .userAddr1("xxxxxx시 xxxx")
                .userAddr2("ㄴㅇㄹㅇㄹㅇ")
                .memberLat(0.00)
                .memberLng(0.00)
                .failedAttempt(0)
                .lockTime(new Date())
                .enabled(true)
                .accountNonLocked(true)
                .role(Role.ROLE_ADMIN)
                .build();
    }

    private Place place(){
        return  Place
                .builder()
                .id(1)
                .placeLng(123.3443)
                .placeLat(23.34322)
                .placeAddr1("xxxx시 xx구")
                .placeAddr2("ㅁㄴㅇㄹ")
                .placeStart("09:00")
                .placeClose("18:00")
                .placeAuthor("admin")
                .placePhone("010-3444-3654")
                .reviewRate(0.0)
                .fileGroupId("place_fre353")
                .placeName("릴렉스")
                .placeImages(placeImages)
                .build();
    }

    private PlaceImage placeImage(){
        return PlaceImage
                .builder()
                .fileGroupId(place().getFileGroupId())
                .thumbFilePath("C:\\\\UploadFile\\\\coffieplace\\images\\thumb\\file_1320441223849700_thumb.jpg")
                .thumbFileImagePath("/istatic/images/coffieplace/images/thumb/1320441218420200_thumb.jpg")
                .imgPath("C:\\\\UploadFile\\\\coffieplace\\images\\1320441218420200.jpg")
                .storedName("다운로드 (1).jpg")
                .originName("1320441218420200.jpg")
                .imgUploader(member.getUserId())
                .imgGroup("coffieplace")
                .isTitle("1")
                .build();
    }

    private PlaceRequestDto placeRequestDto(){
        return new PlaceRequestDto()
                .builder()
                .placeLat(place.getPlaceLat())
                .placeLng(place.getPlaceLng())
                .placeName(place.getPlaceName())
                .placePhone(place.getPlacePhone())
                .placeStart(place.getPlaceStart())
                .placeClose(place.getPlaceClose())
                .placeAddr1(place.getPlaceAddr1())
                .placeAddr2(place.getPlaceAddr2())
                .fileGroupId(place.getFileGroupId())
                .placeAuthor(place.getPlaceAuthor())
                .reviewRate(place.getReviewRate())
                .build();
    }
    private PlaceResponseDto placeResponseDto(){
        return new PlaceResponseDto(
                place.getId(),
                place.getPlaceLng(),
                place.getPlaceLat(),
                place.getReviewRate(),
                place.getPlaceName(),
                place.getPlaceAddr1(),
                place.getPlaceAddr2(),
                place.getPlacePhone(),
                place.getPlaceAuthor(),
                place.getPlaceStart(),
                place.getPlaceClose(),
                place.getFileGroupId(),
                place.getPlaceImageList().size() == 0 ? null : place.getPlaceImageList().get(0).getIsTitle(),
                place.getPlaceImageList().size() == 0 ? null : place.getPlaceImageList().get(0).getImgPath(),
                place.getPlaceImageList().size() == 0 ? null : place.getPlaceImageList().get(0).getThumbFileImagePath());
    }

    private PlaceImageRequestDto placeImageRequestDto(){
        return new PlaceImageRequestDto()
                .builder()
                .images(files)
                .fileType(placeImage.getFileType())
                .imgPath(placeImage.getImgPath())
                .isTitle(placeImage.getIsTitle())
                .originName(placeImage.getOriginName())
                .storedName(placeImage.getStoredName())
                .fileGroupId(placeImage.getFileGroupId())
                .imgGroup(placeImage.getImgGroup())
                .imgUploader(placeImage.getImgUploader())
                .thumbFileImagePath(placeImage.getThumbFileImagePath())
                .thumbFilePath(placeImage.getThumbFilePath())
                .build();
    }

    private PlaceImageResponseDto placeImageResponseDto(){
        return placeImageResponseDto
                .builder()
                .id(placeImage.getId())
                .imgPath(placeImage.getImgPath())
                .imgUploader(placeImage.getImgUploader())
                .isTitle(placeImage.getIsTitle())
                .storedName(placeImage.getStoredName())
                .originName(placeImage.getOriginName())
                .imgGroup(placeImage.getImgGroup())
                .thumbFileImagePath(placeImage.getThumbFileImagePath())
                .thumbFilePath(placeImage.getThumbFilePath())
                .fileGroupId(placeImage.getFileGroupId())
                .fileType(placeImage.getFileType())
                .createdTime(placeImage.getCreatedTime())
                .updatedTime(placeImage.getUpdatedTime())
                .build();
    }
}
