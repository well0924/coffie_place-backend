package com.example.coffies_vol_02.testPlace;

import com.example.coffies_vol_02.config.api.service.KakaoApiSearchService;
import com.example.coffies_vol_02.factory.MemberFactory;
import com.example.coffies_vol_02.factory.PlaceFactory;
import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.config.redis.RedisService;
import com.example.coffies_vol_02.config.util.FileHandler;
import com.example.coffies_vol_02.favoritePlace.service.FavoritePlaceService;
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

    @InjectMocks
    private FavoritePlaceService favoritePlaceService;

    @Mock
    private PlaceImageService placeImageService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private KakaoApiSearchService apiSearchService;

    @Mock
    private RedisService redisService;

    @Mock
    private PlaceImageRepository placeImageRepository;

    @Mock
    private FileHandler fileHandler;

    Member member;

    Place place;

    PlaceImage placeImage;

    PlaceRequestDto placeRequestDto;

    PlaceResponseDto placeResponseDto;

    PlaceImageRequestDto placeImageRequestDto;

    PlaceImageResponseDto placeImageResponseDto;

    List<Place>placeList = new ArrayList<>();

    List<PlaceImage> placeImages = new ArrayList<>();

    List<PlaceImageResponseDto> detailefileList = new ArrayList<>();

    List<PlaceImage>filelist = new ArrayList<>();

    @BeforeEach
    public void init() throws Exception {

        member = MemberFactory.memberDto();
        placeImage = PlaceFactory.placeImage();
        place = PlaceFactory.place();
        place.addPlaceImage(placeImage);
        placeList.add(place);
        placeImages.add(placeImage);
        filelist.add(placeImage);
        fileHandler.placeImagesUpload(PlaceFactory.placeImageRequestDto().getImages());
        placeRequestDto = PlaceFactory.placeRequestDto();
        placeResponseDto = PlaceFactory.placeResponseDto(place);
        placeImageRequestDto = PlaceFactory.placeImageRequestDto();
        placeImageResponseDto = PlaceFactory.placeImageResponseDto();
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
        //given(redisService.getSearchList(place.getPlaceName())).willReturn(listName);
        //when
        when(placeService.listCafePlace(pageRequest,place.getPlaceName(),member)).thenReturn(sliceDto);
        //then
        assertThat(placelist.size()).isNotNull();
    }

    @Test
    @DisplayName("가게 조회-성공")
    public void placeDetailTest() throws Exception {
        //given
        given(placeRepository.findById(anyInt())).willReturn(Optional.of(place));
        //when
        placeService.findCafePlaceById(place.getId());
        //then
        verify(placeRepository).findById(place.getId());
    }

    @Test
    @DisplayName("가게 조회 실패")
    public void placeDetailFailTest(){
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class,()->{
            Optional<Place>detail = Optional.ofNullable(placeRepository.findById(0).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)));
        });
    }

    @Test
    @DisplayName("가게 검색- 성공")
    public void placeSearchTest(){
        String keyword = place.getPlaceName();

        PageRequest pageRequest = PageRequest.of(0,1,Sort.by("id").descending());

        List<PlaceResponseDto>list = new ArrayList<>();
        list.add(placeResponseDto);

        Slice<PlaceResponseDto>placeList = new PageImpl<>(list,pageRequest,0);

        given(placeRepository.placeListSearch(SearchType.p,keyword,pageRequest)).willReturn(placeList);

        when(placeService.searchCafePlace(SearchType.p,keyword,pageRequest,member)).thenReturn(placeList);
        placeList = placeService.searchCafePlace(SearchType.p,keyword,pageRequest,member);

        assertThat(placeList.toList()).isNotEmpty();
    }

    @Test
    @DisplayName("가게 근처 조회-성공")
    public void nearPlaceTest(){
        //given
        List<PlaceResponseDto>placeResponseDtos = new ArrayList<>();
        placeResponseDtos.add(placeResponseDto);
        List<String>placeNames = new ArrayList<>();
        placeNames.add(place.getPlaceName());
        //given
        given(memberRepository.findByUserId(member.getUserId())).willReturn(Optional.of(member));
        given(placeRepository.findPlacesByName(placeNames)).willReturn(placeList);
        given(apiSearchService.extractPlaceName(member)).willReturn(List.of(place.getPlaceName()));

        //when
        when(favoritePlaceService.findByMemberNearList(member)).thenReturn(placeResponseDtos);

        //then
        assertThat(placeResponseDtos).hasSize(1);
    }

    @Test
    @DisplayName("가게 등록")
    public void placeRegisterTest()throws Exception{
        //given
        placeImage = PlaceFactory.placeImage();
        place = PlaceFactory.place();

        placeImage.setPlace(place);

        List<MultipartFile>plImages =new ArrayList<>(List.of(
                new MockMultipartFile("test1", "가게 이미지1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test2", "가게 이미지2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()),
                new MockMultipartFile("test3", "가게 이미지3.PNG", MediaType.IMAGE_PNG_VALUE, "test3".getBytes())));

        given(placeRepository.save(place)).willReturn(place);
        given(fileHandler.placeImagesUpload(plImages)).willReturn(filelist);
        given(placeImageRepository.save(placeImage)).willReturn(placeImage);
        given(fileHandler.ResizeImage(placeImage,240,240)).willReturn("");

        //when
        placeService.createCafePlace(placeRequestDto,placeImageRequestDto);
        //then
        verify(placeRepository,atLeastOnce()).save(any());
        verify(fileHandler,times(2)).placeImagesUpload(any());

    }

    @Test
    @DisplayName("가게 수정")
    public void placeModifyTest()throws Exception{
        //given
        MockMultipartFile updateFile = new MockMultipartFile("test4", "test4.PNG", MediaType.IMAGE_PNG_VALUE, "test4".getBytes());
        List<MultipartFile>files= new ArrayList<>();
        files.add(updateFile);

        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        given(fileHandler.placeImagesUpload(files)).willReturn(placeImages);
        given(placeImageRepository.save(placeImage)).willReturn(placeImage);
        given(placeImageRepository.findPlaceImagePlace(place.getId())).willReturn(placeImages);
        given(fileHandler.ResizeImage(placeImage,240,240)).willReturn("");
        //when
        placeService.updateCafePlace(place.getId(),placeRequestDto,placeImageRequestDto);
        //then
        verify(placeRepository).findById(place.getId());
        verify(placeImageRepository).findPlaceImagePlace(place.getId());
        verify(fileHandler,atLeastOnce()).placeImagesUpload(placeImageRequestDto.getImages());
    }

    @Test
    @DisplayName("가게 삭제")
    public void placeDeleteTest()throws Exception{
        //given
        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        given(placeImageRepository.findPlaceImagePlace(place.getId())).willReturn(placeImages);
        //when
        doNothing().when(placeRepository).deleteById(place.getId());
        placeService.deleteCafePlace(place.getId());
        //then
        verify(placeRepository).deleteById(place.getId());
    }

}
