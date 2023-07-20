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
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import com.example.coffies_vol_02.place.repository.PlaceImageRepository;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import com.example.coffies_vol_02.place.service.PlaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PlaceServiceTest {
    @InjectMocks
    private PlaceService placeService;

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

    private PlaceResponseDto placeResponseDto;

    List<Place>placeList = new ArrayList<>();

    List<PlaceImage> placeImages = new ArrayList<>();

    List<AttachDto> detailefileList = new ArrayList<>();

    List<Attach>filelist = new ArrayList<>();

    @BeforeEach
    public void init(){
        member = memberDto();
        place = place();
        placeList.add(place);
        placeImage = placeImage();
        placeImages.add(placeImage());
        placeResponseDto = placeResponseDto();
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
    public void placeRegisterTest(){
        //given

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
    public void placeDeleteTest(){
        //given

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

    private PlaceImage placeImage(){
        return PlaceImage
                .builder()
                .fileGroupId("place_ereg34593")
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
    private Place place(){
        return Place
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
}
