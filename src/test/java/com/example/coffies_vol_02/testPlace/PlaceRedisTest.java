package com.example.coffies_vol_02.testPlace;

import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.config.redis.CacheKey;
import com.example.coffies_vol_02.config.redis.RedisService;
import com.example.coffies_vol_02.factory.MemberFactory;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceRecentSearchDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PlaceRedisTest {

    @Mock
    RedisOperations<String, String> operations;

    @Mock
    GeoOperations<String,String>geoOperations;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RedisTemplate<String, PlaceRecentSearchDto> redisTemplate;

    @Mock
    private ListOperations<String, PlaceRecentSearchDto> listOperations;

    @InjectMocks
    RedisService redisService;

    private Member member;

    private PlaceRecentSearchDto dto,dto1,dto2;

    @BeforeEach
    public void init(){
        when(operations.opsForGeo()).thenReturn(geoOperations);

        geoOperations.add("CafeStore", new Point(13.361389, 38.115556), "place1");
        geoOperations.add("CafeStore", new Point(15.087269, 37.502669), "place2");
        geoOperations.add("CafeStore", new Point(13.583333, 37.316667), "place3");

        //member
        member = MemberFactory.memberDto();

        dto = PlaceRecentSearchDto.builder()
                .name("test")
                .createdTime(LocalDateTime.now().toString())
                .build();

        dto1 = PlaceRecentSearchDto.builder()
                .name("test1")
                .createdTime(LocalDateTime.now().toString())
                .build();

        dto2 = PlaceRecentSearchDto.builder()
                .name("test2")
                .createdTime(LocalDateTime.now().toString())
                .build();

        when(redisTemplate.opsForList()).thenReturn(listOperations);
    }

    @Test
    //@Disabled
    @DisplayName("geo radius test")
    public void test1(){

        GeoResults<?> mockGeoResults = new GeoResults<>(Collections.emptyList());

        // Mocking GeoOperations.radius method
        when(geoOperations.radius(eq("CafeStore"), any(Circle.class)))
                .thenReturn((GeoResults<RedisGeoCommands.GeoLocation<String>>) mockGeoResults);

        Distance distance = new Distance(100, Metrics.KILOMETERS);
        Circle within = new Circle(new Point(13.583333, 37.316667), distance);

        GeoResults<?> byDistance = geoOperations.radius("CafeStore", within);
        
        assertThat(byDistance.getContent()).hasSize(0);

    }

    @Test
    @DisplayName("가게 검색어 저장기능")
    public void redisAutoCompleteTest(){
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        //key값
        String key = CacheKey.PLACE + member.getId();

        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.leftPush(key,dto)).thenReturn(any());

        redisService.createPlaceNameLog(member.getId(),dto.getName());

        verify(memberRepository).findById(1);

    }

    @Test
    @DisplayName("가게 검색어 목록")
    public void redisAutoCompleteListTest(){
        // Mock 데이터 설정
        when(memberRepository.findById(1)).thenReturn(Optional.of(member));

        // listOperations의 range 메서드 호출 시 원하는 값 반환 설정
        when(listOperations.range(anyString(), eq(0L), eq(5L))).thenReturn(Arrays.asList(dto1, dto2));

        // 테스트할 메서드 호출
        List<PlaceRecentSearchDto> result = redisService.ListPlaceNameLog(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("test1", result.get(0).getName());
        assertEquals("test2", result.get(1).getName());

        verify(memberRepository).findById(1);
        verify(listOperations).range(anyString(), eq(0L), eq(5L));
    }

    @Test
    @DisplayName("회원이 존재하지 않는 경우 예외 처리 테스트")
    public void testListPlaceNameLogMemberNotFound() {
        // Mock 데이터 설정 - 회원이 존재하지 않는 경우
        when(memberRepository.findById(1)).thenReturn(Optional.empty());

        // 예외가 발생하는지 확인
        CustomExceptionHandler exception = Assertions.assertThrows(CustomExceptionHandler.class, () -> redisService.ListPlaceNameLog(1));

        assertEquals(ERRORCODE.NOT_FOUND_MEMBER, exception.getErrorCode());

        verify(memberRepository).findById(1);
        verify(listOperations, never()).range(anyString(), eq(0L), eq(5L));
    }

    @Test
    @DisplayName("가게 검색어 전체삭제 기능 테스트 - 성공")
    public void testDeletePlaceNameLogSuccess() {
        // Mock 데이터 설정
        when(memberRepository.findById(1)).thenReturn(Optional.of(member));
        when(listOperations.remove(anyString(), anyLong(), any(PlaceRecentSearchDto.class))).thenReturn(1L);

        // 테스트할 메서드 호출
        assertDoesNotThrow(() -> redisService.deletePlaceNameLog(1));

        verify(memberRepository).findById(1);
        verify(listOperations).remove(anyString(), eq(1L), any(PlaceRecentSearchDto.class));
    }

    @Test
    @DisplayName("가게 검색어 전체삭제 기능 테스트 - 회원 미존재")
    public void testDeletePlaceNameLogMemberNotFound() {
        // Mock 데이터 설정 - 회원이 존재하지 않는 경우
        when(memberRepository.findById(1)).thenReturn(Optional.empty());

        // 예외가 발생하는지 확인
        CustomExceptionHandler exception = assertThrows(CustomExceptionHandler.class, () -> redisService.deletePlaceNameLog(1));

        assertEquals(ERRORCODE.NOT_FOUND_MEMBER, exception.getErrorCode());

        verify(memberRepository).findById(1);
        verify(listOperations, never()).remove(anyString(), anyLong(), any(PlaceRecentSearchDto.class));
    }

    @Test
    @DisplayName("가게 검색어 전체삭제 기능 테스트 - 검색어 미존재")
    public void testDeletePlaceNameLogSearchLogNotExist() {
        // Mock 데이터 설정
        when(memberRepository.findById(1)).thenReturn(Optional.of(member));
        when(listOperations.remove(anyString(), anyLong(), any(PlaceRecentSearchDto.class))).thenReturn(0L);

        // 예외가 발생하는지 확인
        CustomExceptionHandler exception = assertThrows(CustomExceptionHandler.class, () -> redisService.deletePlaceNameLog(1));

        assertEquals(ERRORCODE.SEARCH_LOG_NOT_EXIST, exception.getErrorCode());

        // Verify that findById, remove methods are called with correct arguments
        verify(memberRepository).findById(1);
        verify(listOperations).remove(anyString(), eq(1L), any(PlaceRecentSearchDto.class));
    }
    
    @Test
    @DisplayName("가게 검색어 개별삭제  기능 테스트 - 성공")
    public void testDeletePlaceNameLogByName(){
        //Mock 데이터 설정
        Integer memberId = 1;
        String key = CacheKey.PLACE + memberId;
        //개별 검색어 로그
        PlaceRecentSearchDto searchDto1 = new PlaceRecentSearchDto("Place1", "2024-07-28T10:15:30");
        PlaceRecentSearchDto searchDto2 = new PlaceRecentSearchDto("TestPlace", "2024-07-28T10:15:30");
        PlaceRecentSearchDto searchDto3 = new PlaceRecentSearchDto("Place3", "2024-07-28T10:15:30");

        List<PlaceRecentSearchDto> searchLogs = new ArrayList<>();
        searchLogs.add(searchDto1);
        searchLogs.add(searchDto2);
        searchLogs.add(searchDto3);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(listOperations.range(key, 0, -1)).thenReturn(searchLogs);

        // When
        redisService.deletePlaceNameLogByName(memberId, "TestPlace");

        // Then
        verify(listOperations, times(1)).remove(key, 1, searchDto2);
    }

    @Test
    @DisplayName("가게 검색어 개별삭제  기능 테스트 - 회원이 없는 경우")
    public void testDeletePlaceNameLogByName_MemberNotFound() {
        // Given
        Integer memberId = 1;
        String searchName = "TestPlace";

        when(memberRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When / Then
        CustomExceptionHandler exception = assertThrows(CustomExceptionHandler.class, () -> {
            redisService.deletePlaceNameLogByName(memberId, searchName);
        });

        assertEquals(ERRORCODE.NOT_FOUND_MEMBER, exception.getErrorCode());
    }

    @Test
    @DisplayName("가게 검색어 개별삭제  기능 테스트 - 검색어가 없는 경우")
    public void testDeletePlaceNameLogByName_SearchLogNotExist() {
        // Given
        Integer memberId = 1;
        String searchName = "TestPlace";
        String key = CacheKey.PLACE + memberId;

        List<PlaceRecentSearchDto> searchLogs = new ArrayList<>();

        when(memberRepository.findById(anyInt())).thenReturn(Optional.of(member));
        when(listOperations.range(key, 0, -1)).thenReturn(searchLogs);

        // When / Then
        CustomExceptionHandler exception = assertThrows(CustomExceptionHandler.class, () -> {
            redisService.deletePlaceNameLogByName(memberId, searchName);
        });

        assertEquals(ERRORCODE.SEARCH_LOG_NOT_EXIST, exception.getErrorCode());
    }

    @Test
    @DisplayName("가게 검색어 개별삭제  기능 테스트 -검색어가 없는 경우")
    public void testDeletePlaceNameLogByName_SearchNameNotExist() {
        // Given
        Integer memberId = 1;
        String searchName = "TestPlace";
        String key = CacheKey.PLACE + memberId;

        PlaceRecentSearchDto searchDto1 = new PlaceRecentSearchDto("Place1", "2024-07-28T10:15:30");
        PlaceRecentSearchDto searchDto3 = new PlaceRecentSearchDto("Place3", "2024-07-28T10:15:30");

        List<PlaceRecentSearchDto> searchLogs = new ArrayList<>();
        searchLogs.add(searchDto1);
        searchLogs.add(searchDto3);

        when(memberRepository.findById(anyInt())).thenReturn(Optional.of(member));
        when(listOperations.range(key, 0, -1)).thenReturn(searchLogs);

        // When / Then
        CustomExceptionHandler exception = assertThrows(CustomExceptionHandler.class, () -> {
            redisService.deletePlaceNameLogByName(memberId, searchName);
        });

        assertEquals(ERRORCODE.SEARCH_LOG_NOT_EXIST, exception.getErrorCode());
    }
}
