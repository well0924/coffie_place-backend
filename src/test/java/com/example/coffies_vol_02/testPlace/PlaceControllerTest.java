package com.example.coffies_vol_02.testPlace;

import com.example.coffies_vol_02.factory.MemberFactory;
import com.example.coffies_vol_02.factory.PlaceFactory;
import com.example.coffies_vol_02.config.TestCustomUserDetailsService;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
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
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class PlaceControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mvc;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private PlaceImageRepository placeImageRepository;

    @MockBean
    private PlaceService placeService;

    private Place place;

    private Member member;

    List<PlaceImage> placeImages = new ArrayList<>();

    private PlaceImage placeImage;

    private PlaceResponseDto placeResponseDto;

    private CustomUserDetails customUserDetails;

    private final TestCustomUserDetailsService testCustomUserDetailsService = new TestCustomUserDetailsService();

    @BeforeEach
    public void init(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        member = MemberFactory.memberDto();
        place = PlaceFactory.place();
        placeImage = PlaceFactory.placeImage();
        placeImages.add(placeImage);
        placeResponseDto = PlaceFactory.placeResponseDto(place);
        customUserDetails = (CustomUserDetails) testCustomUserDetailsService.loadUserByUsername(MemberFactory.memberDto().getUserId());
    }

    @Test
    @DisplayName("가게 목록화면")
    public void placeListTest()throws Exception{

        List<String>listName = new ArrayList<>();
        listName.add(place.getPlaceName());

        List<PlaceResponseDto>placelist = new ArrayList<>();
        placelist.add(placeResponseDto);
        PageRequest pageRequest =  PageRequest.of(0,5, Sort.by("id").descending());

        Slice<PlaceResponseDto>sliceDto = new SliceImpl<>(placelist,pageRequest,true);

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(placeRepository.placeList(pageRequest, String.valueOf(SearchType.all))).willReturn(sliceDto);

        mvc.perform(get("/page/place/list")
                        .contentType(MediaType.TEXT_HTML)
                        .with(user(customUserDetails))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("/place/placelist"))
                .andDo(print());

    }

    @Test
    @DisplayName("가게 조회화면")
    public void placeDetailTest()throws Exception{

        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        given(placeImageRepository.findPlaceImagePlace(place.getId())).willReturn(placeImages);

        when(placeService.findCafePlaceById(place.getId())).thenReturn(placeResponseDto);

        mvc.perform(get("/page/place/detail/"+place.getId())
                .contentType(MediaType.TEXT_HTML)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("detail",placeResponseDto))
                .andExpect(view().name("/place/placedetail"))
                .andDo(print());

        verify(placeService).findCafePlaceById(place.getId());
    }

    @Test
    @DisplayName("가게 등록 화면")
    public void placeRegisterPageTest()throws Exception{

        mvc.perform(get("/page/place/placeregister")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.TEXT_HTML)).andExpect(status().is2xxSuccessful())
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("fileGroupId"))
                .andExpect(view().name("/place/placeregister"))
                .andDo(print());

    }

    @Test
    @DisplayName("가게수정 및 삭제 화면")
    public void placeUpdateDeleteTest()throws Exception{

        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        given(placeImageRepository.findPlaceImagePlace(place.getId())).willReturn(placeImages);

        when(placeService.findCafePlaceById(place.getId())).thenReturn(placeResponseDto);

        mvc.perform(get("/page/place/placemodify/{place-id}",place.getId())
                .contentType(MediaType.TEXT_HTML)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("detail"))
                .andExpect(model().attributeExists("placeImages"))
                .andExpect(view().name("/place/placemodify"))
                .andDo(print());

    }

}
