package com.example.coffies_vol_02.TestPlace;

import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.repository.AttachRepository;
import com.example.coffies_vol_02.attach.service.AttachService;
import com.example.coffies_vol_02.config.util.FileHandler;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.example.coffies_vol_02.place.repository.PlaceImageRepository;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import com.example.coffies_vol_02.place.service.PlaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class PlaceServiceTest {
    @InjectMocks
    private PlaceService placeService;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private PlaceImageRepository placeImageRepository;

    @Mock
    private AttachRepository attachRepository;

    @Mock
    private AttachService attachService;

    @Mock
    private FileHandler fileHandler;
    private Member member;

    Place place;

    PlaceImage placeImage;

    Attach attach;

    List<AttachDto> detailfileList = new ArrayList<>();

    List<Attach>filelist = new ArrayList<>();

    @BeforeEach
    public void init(){

    }

    @Test
    @DisplayName("가게 목록")
    public void placeListTest() throws IOException {
        //given
        //when
        //then
    }

    @Test
    @DisplayName("가게 조회")
    public void placeDetailTest(){
        //given
        //when
        //then
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


}
